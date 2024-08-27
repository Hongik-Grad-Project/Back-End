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
public class MasterOnlyChecker {

    @Before("@annotation(trackers.demo.auth.MasterOnly)")
    public void check(final JoinPoint joinPoint){
        Arrays.stream(joinPoint.getArgs())
                .filter(Accessor.class::isInstance)
                .map(Accessor.class::cast)
                .filter(Accessor::isMember)
                .findFirst()
                .orElseThrow(() -> new AdminException(ExceptionCode.INVALID_ADMIN_AUTHORITY));
    }
}
