package trackers.demo.auth;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.global.exception.AuthException;

import java.util.Arrays;

import static trackers.demo.global.exception.ExceptionCode.INVALID_AUTHORITY;

@Aspect
@Component
public class MemberOnlyChecker {

    @Before("@annotation(trackers.demo.auth.MemberOnly)")
    public void check(final JoinPoint joinPoint){
        Arrays.stream(joinPoint.getArgs())
                .filter(Accessor.class::isInstance)
                .map(Accessor.class::cast)
                .filter(Accessor::isMember)
                .findFirst()
                .orElseThrow(() -> new AuthException(INVALID_AUTHORITY));
    }
}
