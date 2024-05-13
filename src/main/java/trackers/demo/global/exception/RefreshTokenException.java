package trackers.demo.global.exception;

import lombok.Getter;

@Getter
public class RefreshTokenException extends AuthException{
    public RefreshTokenException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
