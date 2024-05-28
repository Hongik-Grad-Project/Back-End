package trackers.demo.loginv2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import trackers.demo.loginv2.domain.repository.RefreshTokenRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // 매일 자정에 실행되도록 스케줄링
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteAllTokens(){
        // 현재 시간 기준으로 하루 전 시간 계산
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        // 하루 전의 시간 이전에 생성된 모든 토큰 삭제
        refreshTokenRepository.deleteByCreatedAtBefore(yesterday);
    }


}
