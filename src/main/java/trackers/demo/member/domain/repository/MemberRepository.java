package trackers.demo.member.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trackers.demo.member.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findBySocialLoginId(String socialLoginId);

    @Modifying
    @Query("""
            UPDATE Member member
            SET member.status = 'DELETED'
            WHERE member.id = :memberId
            """)
    void deleteByMemberId(@Param("memberId") final Long memberId);

    boolean existsByNickname(final String nicknameWithRandomNumber);
}
