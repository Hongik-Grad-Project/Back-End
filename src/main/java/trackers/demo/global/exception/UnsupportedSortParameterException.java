package trackers.demo.global.exception;

import lombok.Getter;

@Getter
public class UnsupportedSortParameterException extends BadRequestException{

    public UnsupportedSortParameterException(ExceptionCode exceptionCode){ super(exceptionCode);}
}
