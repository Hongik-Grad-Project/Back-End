package trackers.demo.global.exception;

import lombok.Getter;

@Getter
public class InvalidJwtException extends AuthException{

    public InvalidJwtException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
