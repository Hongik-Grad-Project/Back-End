package trackers.demo.admin.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import trackers.demo.admin.domain.AdminMember;

import static lombok.AccessLevel.*;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class AdminMemberResponse {

    private final Long id;

    private final String username;

    private final String adminType;

    public static AdminMemberResponse from(final AdminMember adminMember){
        return new AdminMemberResponse(
                adminMember.getId(),
                adminMember.getUsername(),
                adminMember.getAdminType().name()
        );
    }
}
