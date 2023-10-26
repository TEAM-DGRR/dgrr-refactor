package live.dgrr.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    protected ResponseEntity<ErrorResponse> handleRestException(GeneralException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return new ResponseEntity<>(ErrorResponse.of(ex.getErrorCode()),errorCode.getHttpStatus());
    }
}
