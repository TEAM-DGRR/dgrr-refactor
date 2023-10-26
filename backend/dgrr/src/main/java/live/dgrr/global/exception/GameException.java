package live.dgrr.global.exception;

import lombok.Getter;

@Getter
public class GameException extends RuntimeException{

    private final ErrorCode errorCode;

    public GameException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
