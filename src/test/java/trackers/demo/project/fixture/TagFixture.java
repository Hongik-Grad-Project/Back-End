package trackers.demo.project.fixture;

import trackers.demo.project.domain.ProjectTag;
import trackers.demo.project.domain.Tag;

import static trackers.demo.project.fixture.ProjectFixture.DUMMY_PROJECT_NOT_COMPLETED;

public class TagFixture {

    public static final Tag DUMMY_TAG = new Tag(
            1L,
            "모두의 교육"
    );

    public static final ProjectTag DUMMY_PROJECT_TAG = new ProjectTag(
            1L,
            DUMMY_PROJECT_NOT_COMPLETED,
            DUMMY_TAG
    );
}
