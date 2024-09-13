package trackers.demo.note.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trackers.demo.auth.Auth;
import trackers.demo.auth.MemberOnly;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.note.dto.response.DetailNoteResponse;
import trackers.demo.note.dto.response.SimpleNoteResponse;
import trackers.demo.note.service.NoteService;

import java.util.List;

@RestController
@RequestMapping("/note")
@RequiredArgsConstructor
@Slf4j
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    @MemberOnly
    public ResponseEntity<List<SimpleNoteResponse>> getNotes(@Auth final Accessor accessor){
        log.info("memberId={}의 요약 노트 내역 조회 요청이 들어왔습니다.", accessor.getMemberId());
        final List<SimpleNoteResponse> noteResponses = noteService.getNotes(accessor.getMemberId());
        return ResponseEntity.ok(noteResponses);
    }

    @GetMapping("/{noteId}")
    @MemberOnly
    public ResponseEntity<DetailNoteResponse> getNote(
            @Auth final Accessor accessor,
            @PathVariable("noteId") final Long noteId
    ){
        log.info("memberId={}의 요약 노트 상세 조회 요청이 들어왔습니다.", accessor.getMemberId());
        noteService.validateNoteByMemberId(accessor.getMemberId(), noteId);
        final DetailNoteResponse detailNoteResponse = noteService.getNote(noteId);
        return ResponseEntity.ok(detailNoteResponse);
    }

    @DeleteMapping("/{noteId}")
    @MemberOnly
    public ResponseEntity<Void> deleteNote(
            @Auth final Accessor accessor,
            @PathVariable("noteId") final Long noteId
    ){
        log.info("memberId={}의 noteId ={}의 삭제 요청이 들어왔습니다.", accessor.getMemberId(), noteId);
        noteService.validateNoteByMemberId(accessor.getMemberId(), noteId);
        noteService.delete(noteId);
        return ResponseEntity.noContent().build();
    }

    // todo: 요약 노트 수정 ( POST: /note/{noteId} ) 보류 ...

}
