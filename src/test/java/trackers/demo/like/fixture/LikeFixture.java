package trackers.demo.like.fixture;

import trackers.demo.like.dto.LikeInfo;
import trackers.demo.like.dto.response.LikeResponse;
import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.MemberState;

import java.time.LocalDateTime;

import static trackers.demo.global.common.entity.type.StatusType.USABLE;

public class LikeFixture {

    public static final LikeResponse DUMMY_LIKE_RESPONSE = new LikeResponse(
            5L,
            true
    );
}
