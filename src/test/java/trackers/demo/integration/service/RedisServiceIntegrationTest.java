package trackers.demo.integration.service;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import trackers.demo.global.restdocs.RedisTestConfig;
import trackers.demo.member.domain.Member;
import trackers.demo.member.domain.repository.MemberRepository;

@SpringBootTest
@Sql(value = {
        "classpath:data/init.sql",
})
@Import(RedisTestConfig.class)
public class RedisServiceIntegrationTest {

    @Autowired
    protected MemberRepository memberRepository;

    public Member member;

    @BeforeEach
    void setMember(){
        this.member = memberRepository.save(new Member(
                "socialLoginId",
                "email"
        ));
    }

}
