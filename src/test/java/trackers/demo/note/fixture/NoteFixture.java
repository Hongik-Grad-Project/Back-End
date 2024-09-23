package trackers.demo.note.fixture;

import trackers.demo.note.domain.Note;
import trackers.demo.note.dto.response.DetailNoteResponse;
import trackers.demo.note.dto.response.ProjectProposalResponse;
import trackers.demo.note.dto.response.SimpleNoteResponse;

import java.time.LocalDateTime;
import java.util.List;

public class NoteFixture {

    public static SimpleNoteResponse DUMMY_NOTE_RESPONSE_1 = new SimpleNoteResponse(
            1L,
            "환경 문제 해결 노트",
            LocalDateTime.now()
    );

    public static SimpleNoteResponse DUMMY_NOTE_RESPONSE_2 = new SimpleNoteResponse(
            2L,
            "청년 실업 문제 해결 노트",
            LocalDateTime.now()
    );

    public static DetailNoteResponse DUMMY_DETAIL_NOTE_RESPONSE = new DetailNoteResponse(
            1L,
            "사회 문제 해결을 위한 캠페인 참여자",
            "환경 오염으로 인한 이상 기후 현상",
            "이상 기온 체험 캠페인",
            List.of("온도 변화를 체험하는 부스",
                    "20년의 트렌드와 기후 변화",
                    "신나는 환경 교육 캠페인"),
            List.of("각 부스에서 20년 동안의 기온 변화를 체험할 수 있도록 구성.",
                    "유행했던 노래와 화제의 사건을 중심으로 부스 꾸미기.",
                    "체감 온도를 조절하는 방법으로 온도 조절 장치와 시각적 효과 활용."),
            "부스를 통해 체험하며 사람이 더위와 환경 문제의 심각성을 인식하게끔 유도."
    );

    public static ProjectProposalResponse DUMMY_PROJECT_PROPOSAL = new ProjectProposalResponse(
            "우리사회",
            "환경 오염으로 인한 이상 기후 현상 증가와 그에 대한 경각심 부족",
            "이상 기후 체험 캠페인",
            List.of("자동완성 소제목1", "자동완성 소제목2"),
            List.of("자동완성 본문1", "자동완성 본문2")
    );

}
