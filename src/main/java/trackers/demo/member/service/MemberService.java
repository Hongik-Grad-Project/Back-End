package trackers.demo.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.global.exception.BadRequestException;
import trackers.demo.global.exception.ExceptionCode;
import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.repository.MemberRepository;
import trackers.demo.member.dto.request.MyProfileUpdateRequest;

import static trackers.demo.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public void validateProfileByMember(final Long memberId) {
        if(!memberRepository.existsById(memberId)){
            throw new BadRequestException(NOT_FOUND_MEMBER_ID);
        }
    }

    public void updateProfile(
            final Long memberId,
            final MyProfileUpdateRequest updateRequest,
            final String newImageUrl
    ) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        String persistImageUrl = member.getProfileImage();

        if(newImageUrl != null){
            persistImageUrl = newImageUrl;
            // todo: 기존 프로필 이미지 S3에서도 지우기
        }

        member.updateProfile(
                updateRequest.getNickname(),
                newImageUrl,
                updateRequest.getIntroduction()
        );
        memberRepository.save(member);
    }
}
