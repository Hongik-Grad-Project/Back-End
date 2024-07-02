package trackers.demo.project.fixture;

import trackers.demo.project.domain.ProjectSubject;
import trackers.demo.project.domain.Subject;

import static trackers.demo.project.fixture.ProjectFixture.DUMMY_PROJECT;

public class SubjectFixture {

    public static final Subject DUMMY_SUBJECT = new Subject(
            8L,
            "더나은 사회"
    );

    public static final ProjectSubject DUMMY_PROJECT_SUBJECT = new ProjectSubject(
            1L,
            DUMMY_PROJECT,
            DUMMY_SUBJECT
    );
}
