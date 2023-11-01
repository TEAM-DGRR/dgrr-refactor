package live.dgrr.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
    MEMBER_ROOM_MAPPING_NOT_FOUND(HttpStatus.NOT_FOUND, "MemberRoomMapping Not Found"), //멤버Id로 대기 방 Id를 찾을 수 없는 경우
    GAME_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "GameRoom Not Found"),
    IS_NOT_ROOM_MANAGER(HttpStatus.FORBIDDEN, "Is Not Room Manager"),
    NOT_ENOUGH_MEMBERS_TO_START(HttpStatus.BAD_REQUEST, "Not Enough Members To Start"), //대기 방에 2명 미만인데 시작을 누른경우
    MEMBER_NOT_READY_YET(HttpStatus.FORBIDDEN, "Member Not Ready Yet"), //대기 방에 있는 사람이 준비상태가 아닌 경우
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "File Not Found"), // yml 파일 등이 없을 때
    WAITING_ROOM_ALREADY_START(HttpStatus.BAD_REQUEST, "WaitingRoom Already Start"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Member Not Found"),
    WAITING_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "WaitingRoom Not Found"),
    MEMBER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "Member Already Exist"), //동일한 멤버가 이미 방에 존재하는 경우
    MAX_MEMBER_ALREADY_EXIST(HttpStatus.CONFLICT, "Max Member Already Exist"), //대기 방 참여자 정원이 다 찬 경우
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Image Not Found"), // 이미지를 찾을 수 없을 때
    IMAGE_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error Processing Image"), // 이미지 처리 중 오류났을 때
    OPPONENT_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Opponent Member Not Found"),
    OPENVIDU_SESSION_ERROR(HttpStatus.BAD_REQUEST, "Openvidu Session Create Error"),
    OPENVIDU_CONNECTION_ERROR(HttpStatus.BAD_REQUEST, "Openvidu Connection Create Error");

    private final HttpStatus httpStatus;
    private final String message;
}
