package trackers.demo.loginv2.infrastructure;

import org.springframework.stereotype.Component;
import trackers.demo.global.exception.ExceptionCode;
import trackers.demo.global.exception.InvalidJwtException;

import static trackers.demo.global.exception.ExceptionCode.*;

// HTTP 요청 헤더에서 Bearer 토큰 추출
@Component
public class BearerAuthorizationExtractor {

    private static final String BEARER_TYPE = "Bearer ";

    public String extractAccessToken(String header){
        if(header != null && header.startsWith(BEARER_TYPE)){
            return header.substring(BEARER_TYPE.length()).trim();
        }
        throw new InvalidJwtException(INVALID_ACCESS_TOKEN);
    }

}
