package trackers.demo.global.exception;

import lombok.Getter;

@Getter
public class InvalidDomainException extends BadRequestException{
    public InvalidDomainException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
