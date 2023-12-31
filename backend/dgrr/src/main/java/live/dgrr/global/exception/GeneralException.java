package live.dgrr.global.exception;

import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {
    private final ErrorCode errorCode;

    public GeneralException(ErrorCode errorCode, Exception exception) {
        super(errorCode.getMessage(), exception);
        this.errorCode = errorCode;
    }

    public GeneralException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
