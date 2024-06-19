package trackers.demo.auth;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import trackers.demo.auth.domain.Accessor;
import trackers.demo.global.exception.AdminException;
import trackers.demo.global.exception.ExceptionCode;

import java.util.Arrays;

@Aspect
@Component
public class AdminOnlyChecker {

    @Before("@annotation(trackers.demo.auth.AdminOnly)")
    public void check(final JoinPoint joinPoint){   // JoinPoint: 현재 실행 중인 메서드에 대한 메타데이터를 제공
        Arrays.stream(joinPoint.getArgs())
                .filter(Accessor.class::isInstance)
                .map(Accessor.class::cast)
                .filter(Accessor::isAdmin)
                .findFirst()
                .orElseThrow(() -> new AdminException(ExceptionCode.INVALID_ADMIN_AUTHORITY));
    }
}
