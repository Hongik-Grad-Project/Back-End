package trackers.demo.note.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.admin.domain.Assistant;
import trackers.demo.admin.domain.repository.AssistantRepository;
import trackers.demo.chat.domain.ChatRoom;
import trackers.demo.chat.domain.repository.ChatRoomRepository;
import trackers.demo.global.config.ChatGPTConfig;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.note.domain.repository.NoteRepository;

import static trackers.demo.global.exception.ExceptionCode.NOT_FOUND_ASSISTANT;
import static trackers.demo.global.exception.ExceptionCode.NOT_FOUND_CHAT_ROOM;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NoteService {

    private final ChatGPTConfig config;

    private final NoteRepository noteRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AssistantRepository assistantRepository;


}
