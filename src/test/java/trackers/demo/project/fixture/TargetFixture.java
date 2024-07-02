package trackers.demo.project.fixture;

import trackers.demo.project.domain.ProjectSubject;
import trackers.demo.project.domain.ProjectTarget;
import trackers.demo.project.domain.Target;

import static trackers.demo.project.fixture.ProjectFixture.DUMMY_PROJECT;

public class TargetFixture {

    public static final Target DUMMY_TARGET = new Target(
            5L,
            "실버 세대"
    );

    public static final ProjectTarget DUMMY_PROJECT_TARGET = new ProjectTarget(
            1L,
            DUMMY_PROJECT,
            DUMMY_TARGET
    );
}
