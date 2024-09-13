package trackers.demo.note.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.admin.domain.repository.AssistantRepository;
import trackers.demo.chat.domain.ChatRoom;
import trackers.demo.chat.domain.repository.ChatRoomRepository;
import trackers.demo.global.config.ChatGPTConfig;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.note.domain.Note;
import trackers.demo.note.domain.repository.CustomNoteRepository;
import trackers.demo.note.domain.repository.NoteRepository;
import trackers.demo.note.dto.response.DetailNoteResponse;
import trackers.demo.note.dto.response.SimpleNoteResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static trackers.demo.global.exception.ExceptionCode.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NoteService {

    private final ChatGPTConfig config;

    private final NoteRepository noteRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AssistantRepository assistantRepository;
    private final CustomNoteRepository customNoteRepository;

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
        noteRepository.deleteById(noteId);
    }
}
