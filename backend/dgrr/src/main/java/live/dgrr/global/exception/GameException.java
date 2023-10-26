package live.dgrr.global.exception;

public class GameException extends RuntimeException{

    private final ErrorCode errorCode;

    public GameException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
