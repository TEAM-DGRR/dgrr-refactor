package live.dgrr.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
    MEMBER_ROOM_MAPPING_NOT_FOUND(HttpStatus.NOT_FOUND, "Member_Room_Mapping Not Found"), //멤버Id로 대기 방 Id를 찾을 수 없는 경우
    GAME_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "GameRoom Not Found");
    private final HttpStatus httpStatus;
    private final String message;
}
