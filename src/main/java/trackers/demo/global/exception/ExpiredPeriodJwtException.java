package trackers.demo.global.exception;

public class ExpiredPeriodJwtException extends AuthException{
    public ExpiredPeriodJwtException(final ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
