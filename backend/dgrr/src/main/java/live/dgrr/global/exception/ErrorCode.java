package live.dgrr.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
    MEMBER_ROOM_MAPPING_NOT_FOUND(HttpStatus.NOT_FOUND, "Member_Room_Mapping Not Found"), //멤버Id로 대기 방 Id를 찾을 수 없는 경우
    GAME_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "GameRoom Not Found"),
    IS_NOT_ROOM_MANAGER(HttpStatus.FORBIDDEN, "Is Not Room Manager"),
    NOT_ENOUGH_MEMBERS_TO_START(HttpStatus.BAD_REQUEST, "Not Enough Members To Start"), //대기 방에 2명 미만인데 시작을 누른경우
    MEMBER_NOT_READY_YET(HttpStatus.FORBIDDEN, "Member Not Ready Yet"), //대기 방에 있는 사람이 준비상태가 아닌 경우
    WAITING_ROOM_ALREADY_START(HttpStatus.BAD_REQUEST, "WaitingRoom Already Start");
    private final HttpStatus httpStatus;
    private final String message;
}
