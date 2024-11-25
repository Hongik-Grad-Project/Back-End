package trackers.demo.note.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import trackers.demo.admin.domain.Assistant;
import trackers.demo.admin.domain.repository.AssistantRepository;
import trackers.demo.chat.domain.ChatRoom;
import trackers.demo.chat.domain.Message;
import trackers.demo.chat.domain.repository.ChatRoomRepository;
import trackers.demo.chat.domain.repository.MessageRepository;
import trackers.demo.chat.dto.request.CompletionMessage;
import trackers.demo.chat.dto.request.CompletionRequest;
import trackers.demo.chat.dto.response.CompletionResponse;
import trackers.demo.chat.dto.response.MessageResponse;
import trackers.demo.chat.dto.response.RunResponse;
import trackers.demo.chat.dto.response.ThreadMessageResponse;
import trackers.demo.global.config.ChatGPTConfig;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.repository.MemberRepository;
import trackers.demo.note.domain.Note;
import trackers.demo.note.domain.repository.CustomNoteRepository;
import trackers.demo.note.domain.repository.NoteRepository;
import trackers.demo.note.dto.response.AutomatedProposalResponse;
import trackers.demo.note.dto.response.DetailNoteResponse;
import trackers.demo.note.dto.response.ProjectProposalResponse;
import trackers.demo.note.dto.response.SimpleNoteResponse;
import trackers.demo.project.domain.Project;
import trackers.demo.project.domain.ProjectTarget;
import trackers.demo.project.domain.Target;
import trackers.demo.project.domain.repository.ProjectRepository;
import trackers.demo.project.domain.repository.ProjectTargetRepository;
import trackers.demo.project.domain.repository.TargetRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static trackers.demo.chat.domain.type.ChatMessageRole.ASSISTANT;
import static trackers.demo.chat.domain.type.ChatMessageRole.USER;
import static trackers.demo.chat.domain.type.SenderType.AURORA_AI;
import static trackers.demo.chat.domain.type.SenderType.MEMBER;
import static trackers.demo.global.exception.ExceptionCode.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NoteService {

    private static final String AURORA_AI_PROPOSAL_BOT = "오로라 AI 기획서 생성";
    private static final String AUTO_COMPLETE_MESSAGE = "대화 내용을 바탕으로 기획서를 자동완성 해줘";
    private static final String DEFAULT_IMAGE_URL = "https://image.myaurora.co.kr/dev/a0606c166df331b54f8731caef9bbe5cc9b953f57586e2dc1ecdd73d85586cae.png";

    private final ChatGPTConfig config;

    private final NoteRepository noteRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AssistantRepository assistantRepository;
    private final CustomNoteRepository customNoteRepository;
    private final TargetRepository targetRepository;
    private final MemberRepository memberRepository;
    private final ProjectTargetRepository projectTargetRepository;
    private final ProjectRepository projectRepository;
    private final MessageRepository messageRepository;

    private String PROPOSAL_AI_ROLE;

    @PostConstruct
    public void init() {
        try {
            PROPOSAL_AI_ROLE = readFile("roles/proposal_ai_role");
        } catch (IOException e) {
            log.error("Error reading AI role files", e);
            throw new IllegalStateException("Failed to initialize AI roles", e);
        }
    }

    @Transactional(readOnly = true)
    public List<SimpleNoteResponse> getNotes(Long memberId) {
        final List<ChatRoom> chatRooms = chatRoomRepository.findByMemberIdAndIsSummarized(memberId, true);

        final List<Note> notes = chatRooms.stream()
                .map(chatRoom -> noteRepository.findByChatRoomId(chatRoom.getId()))
                .filter(Objects::nonNull)
                .toList();

        return notes.stream()
                .map(SimpleNoteResponse::of)
                .collect(Collectors.toList());
    }

    public void validateNoteByMemberId(final Long memberId, final Long noteId) {
        final Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_NOTE));

        final ChatRoom chatRoom = note.getChatRoom();

        if(!chatRoomRepository.existsByMemberIdAndId(memberId, chatRoom.getId())) {
            throw new BadRequestException(NOT_FOUND_NOTE_BY_MEMBER_ID);
        }
    }

    @Transactional(readOnly = true)
    public DetailNoteResponse getNote(Long noteId) {
        final Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_NOTE));
        return DetailNoteResponse.of(note);
    }

    public void delete(final Long noteId) {
        final Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_NOTE));

        final ChatRoom chatRoom = note.getChatRoom();
        chatRoom.deleteNote();
        chatRoom.updateIsSummarized(false);

        log.info("노트 삭제");
        noteRepository.deleteById(noteId);
    }

    public ProjectProposalResponse getAutomatedProposalV1(final Long memberId, final Long noteId) throws JsonProcessingException {
        final Note note = noteRepository.findById(noteId).orElseThrow(() -> new BadRequestException(NOT_FOUND_CHAT_ROOM));
        final ChatRoom chatRoom = note.getChatRoom();
        final Member member = memberRepository.findById(memberId).orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

        final String receivedMessage = getProposalCompletion(AUTO_COMPLETE_MESSAGE, chatRoom.getId());

        return createProjectProposal(receivedMessage, note, member);
    }

    private String getProposalCompletion(final String prompt, final Long chatRoomId) {
        CompletionRequest request = null;

        final List<CompletionMessage> chatGPTMessages = new ArrayList<>();
        final List<Message> messages = messageRepository.findAllByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
        for(final Message message : messages){
            CompletionMessage historyMessage = null;
            if(message.getSenderType().equals(AURORA_AI)){
                historyMessage = new CompletionMessage(ASSISTANT.value(), message.getContents());
            } else if (message.getSenderType().equals(MEMBER)) {
                historyMessage = new CompletionMessage(USER.value(), message.getContents());
            }
            chatGPTMessages.add(historyMessage);
            request = new CompletionRequest(PROPOSAL_AI_ROLE, config.getModel(), chatGPTMessages, prompt);
        }

        log.info("RestTemplate으로 ChatGPT API에 POST 요청");
        CompletionResponse completionResponse = config.completionTemplate().postForObject(
                config.getCompletionApiUrl(),
                request,
                CompletionResponse.class
        );

        log.info("응답 받기 성공");
        String response = completionResponse.getChoices().get(0).getMessage().getContent();

//        // 토큰 사용량 분석용 코드
//        int prompt_tokens = completionResponse.getUsage().getPrompt_tokens();
//        log.info("prompt_tokens = {}", prompt_tokens);
//        int completion_tokens = completionResponse.getUsage().getCompletion_tokens();
//        log.info("completion_tokens = {}", completion_tokens);
//        int total_tokens = completionResponse.getUsage().getTotal_tokens();
//        log.info("total_tokens = {}", total_tokens);

        return response;
    }


    public ProjectProposalResponse getAutomatedProposalV2(final Long memberId, final Long noteId)
            throws InterruptedException, JsonProcessingException {
        final Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_CHAT_ROOM));

        final ChatRoom chatRoom = note.getChatRoom();

        final Assistant assistant = assistantRepository.findByName(AURORA_AI_PROPOSAL_BOT)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ASSISTANT));

        final Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

        log.info("1. 사용자 메시지 생성하기");
        final MessageResponse messageResponse = sendMessage(chatRoom.getThread(), AUTO_COMPLETE_MESSAGE);

        log.info("2. Run 객체 실행");
        final RunResponse initialRunResponse = createRun(assistant.getAssistantId(), chatRoom.getThread());

        log.info("3. Run 객체 갱신");
        final RunResponse completedRunResponse = updateRun(initialRunResponse.getId(), chatRoom.getThread());

        log.info("4. 응답 메시지 추출");
        final ThreadMessageResponse.Message lastAssistantMessage = getThreadMessage(chatRoom.getThread());
        final String receivedMessage = lastAssistantMessage.getContent().get(0).getText().getValue();

        log.info("5. DTO 생성 및 프로젝트 저장");
        final ProjectProposalResponse response = createProjectProposal(receivedMessage, note, member);

        log.info("6. Thread에서 마지막 메시지 두개 지우기");
        deleteMessageInThread(chatRoom.getThread(), messageResponse.getId(), lastAssistantMessage.getId());

        return response;
    }

    private MessageResponse sendMessage(final String threadId, final String content) {
        final String url = UriComponentsBuilder.fromHttpUrl(config.getMessageApiUrl())
                .buildAndExpand(threadId)
                .toUriString();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("role", "user");
        requestBody.put("content", content);

        final MessageResponse response = config.assistantTemplate().postForObject(
                url,
                requestBody,
                MessageResponse.class
        );
        return response;
    }

    private RunResponse createRun(final String assistantId, final String threadId) {

        final String url = UriComponentsBuilder.fromHttpUrl(config.getRunApiUrl())
                .buildAndExpand(threadId)
                .toUriString();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("assistant_id", assistantId);

        final RunResponse response = config.assistantTemplate().postForObject(
                url,
                requestBody,
                RunResponse.class
        );
        return response;
    }

    private RunResponse updateRun(final String runId, final String threadId) throws InterruptedException {
        final String url = UriComponentsBuilder.fromHttpUrl(config.getRunApiUrl() + "/" + runId)
                .buildAndExpand(threadId)
                .toUriString();

        final long startTime = System.currentTimeMillis();

        while (true) {
            final RunResponse retrievedRun = config.assistantTemplate().getForObject(url, RunResponse.class);

            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            log.info("Run status: {}, 경과: {}초", retrievedRun.getStatus(), (float) elapsedTime);

            // 상태가 completed일 경우 종료
            if ("completed".equals(retrievedRun.getStatus())) {
                return retrievedRun;
            }

            // 상태가 [실패, 취소, 만료]된 경우 예외 발생
            if (retrievedRun.getStatus().equals("failed") ||
                    retrievedRun.getStatus().equals("cancelled") ||
                    retrievedRun.getStatus().equals("expired")) {
                throw new RuntimeException("Run failed: " + retrievedRun.getLast_error());
            }

            // 1초 대기
            Thread.sleep(1000);
        }
    }

    private ThreadMessageResponse.Message getThreadMessage(final String threadId) {
        final String url = UriComponentsBuilder.fromHttpUrl(config.getThreadMessageApiUrl())
                .buildAndExpand(threadId)
                .toUriString();

        final ThreadMessageResponse response = config.assistantTemplate().getForObject(
                url,
                ThreadMessageResponse.class
        );

        final List<ThreadMessageResponse.Message> messages = response.getData();

        final List<ThreadMessageResponse.Message> assistantMessages = messages.stream()
                .filter(message -> "assistant".equals(message.getRole()))  // AI의 메시지만 선택
                .collect(Collectors.toList());

        log.info("assistantMessages.size() = {}", assistantMessages.size());
        log.info("messages.size() = {}", messages.size());

        // 첫 번째 메시지의 내용을 반환
        if (!assistantMessages.isEmpty()) {
            return assistantMessages.get(0);
//            return lastAssistantMessage.getContent().get(0).getText().getValue();
        } else {
            throw new BadRequestException(NOT_FOUND_MESSAGE_IN_THREAD);
        }
    }

    private ProjectProposalResponse createProjectProposal(
            final String receivedMessage,
            final Note note,
            final Member member
    ) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        final String trimmedMessage = receivedMessage.replaceAll(".*(\\{.*\\}).*", "$1");

        log.info("정제 전 기획서 응답: {}", receivedMessage);
        log.info("정제 후 기획서 응답: {}", trimmedMessage);

        final AutomatedProposalResponse automatedProposalResponse
                = objectMapper.readValue(trimmedMessage, AutomatedProposalResponse.class);

        final Project project = Project.createProject(
                member,
                note.getProblem(),
                LocalDate.now(),
                LocalDate.now().plusDays(100),
                note.getTitle(),
                DEFAULT_IMAGE_URL,
                automatedProposalResponse.getTitleList(),
                automatedProposalResponse.getContentList()
        );
        final Project newProject = projectRepository.save(project);

        final Target target = targetRepository.findByTargetTitle(note.getTarget())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TARGET));
        final ProjectTarget newProjectTarget = new ProjectTarget(null, newProject, target);
        projectTargetRepository.save(newProjectTarget);

        return ProjectProposalResponse.of(newProject.getId(), automatedProposalResponse, note);
    }

    private void deleteMessageInThread(final String threadId, final String memberMessageId, final String aiMessageId) {
        String memberMessageUrl = UriComponentsBuilder.fromHttpUrl(config.getDeleteMessageApiUrl())
                .buildAndExpand(threadId, memberMessageId)
                .toUriString();
        config.assistantTemplate().delete(memberMessageUrl);

        String aiMessageUrl = UriComponentsBuilder.fromHttpUrl(config.getDeleteMessageApiUrl())
                .buildAndExpand(threadId, aiMessageId)
                .toUriString();
        config.assistantTemplate().delete(aiMessageUrl);
    }

    private String readFile(final String filePath) throws IOException {
        Path path = new ClassPathResource(filePath).getFile().toPath();
        return Files.readString(path);
    }
}
