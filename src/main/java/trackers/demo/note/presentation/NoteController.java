package trackers.demo.note.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trackers.demo.auth.Auth;
import trackers.demo.auth.MemberOnly;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.note.service.NoteService;

import java.net.URI;

@RestController
@RequestMapping("/note")
@RequiredArgsConstructor
@Slf4j
public class NoteController {

    private final NoteService noteService;


    // todo: 요약 노트 수정 ( POST: /note/{noteId} )

    // todo: 요약 노트 조회 ( GET: /note/{noteId} ) -> 다른 요약 노트 리스트도 반환

    // todo: 요약 노트 삭제 ( DELETE: /note/{noteId} )
}
