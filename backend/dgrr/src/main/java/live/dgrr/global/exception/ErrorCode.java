package live.dgrr.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
    GAME_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "GameRoom Not Found");
    private final HttpStatus httpStatus;
    private final String message;
}
