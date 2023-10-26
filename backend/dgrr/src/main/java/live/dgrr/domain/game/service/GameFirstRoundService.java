package live.dgrr.domain.game.service;

import live.dgrr.domain.game.GameStartDto;
import live.dgrr.domain.game.entity.GameMember;
import live.dgrr.domain.game.entity.GameRoom;
import live.dgrr.domain.game.entity.GameStatus;
import live.dgrr.domain.game.entity.event.FirstRoundPreparedEvent;
import live.dgrr.domain.game.entity.event.RoundOverEvent;
import live.dgrr.domain.game.repository.GameRoomRepository;
import live.dgrr.domain.openvidu.OpenviduService;
import live.dgrr.global.entity.Rank;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GameFirstRoundService {

    private final OpenviduService openviduService;
    private final GameRoomRepository gameRoomRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    private static final long ROUND_TIME = 5000L;

    /**
     *  게임 시작 시 동작하는 메소드
     * @param memberOneId 멤버1아이디
     * @param memberTwoId 멤버2 아이디
     */
    public List<GameStartDto> gameStart(String memberOneId, String memberTwoId) {
        GameMember memberOne = new GameMember(memberOneId, memberOneId," ", "descript1", 1400, Rank.BRONZE);
        GameMember memberTwo = new GameMember(memberTwoId, memberTwoId," ", "descript2", 1400, Rank.GOLD);

        //게임 ID 생성
        String gameRoomId = UUID.randomUUID().toString();

        //오픈비두 세션 생성, 토큰 생성
        String openviduSessionId = openviduService.createSession(gameRoomId);
        String openviduTokenOne = openviduService.createConnection(openviduSessionId);
        String openviduTokenTwo = openviduService.createConnection(openviduSessionId);

        //Redis 에 게임 객체 저장
        GameRoom gameRoom = new GameRoom(gameRoomId, memberOne, memberTwo, GameStatus.BEFORE_START);
        gameRoomRepository.save(gameRoom);

        //GameStart 정보 전송
        GameStartDto memberOneGameStartDto = new GameStartDto(memberOne,memberTwo,gameRoomId,openviduTokenOne,"FIRST");
        GameStartDto memberTwoGameStartDto = new GameStartDto(memberTwo,memberOne,gameRoomId,openviduTokenTwo,"SECOND");

        return List.of(memberOneGameStartDto, memberTwoGameStartDto);
    }

    /**
     * 1라운드 준비 신호
     * @param gameRoomId 게임 룸 아이디
     */
    public void prepareFirstRoundStart(String gameRoomId) {
        GameRoom gameRoom = gameRoomRepository.findById(gameRoomId).orElseThrow(() -> new RuntimeException());
        int prepareCounter = gameRoom.firstRoundPrepare();
        gameRoomRepository.save(gameRoom);

        //todo: 동시성 이슈 처리 필요
        //둘 모두 준비되었을때만 시작
        if(prepareCounter == 2) {
            firstRoundStart(gameRoomId, gameRoom);
        }
    }

    /**
     * 1라운드 시작을 알리는 메소드
     * @param gameRoomId 게임 룸 아이디
     * @param gameRoom 게임룸 객체
     */
    public void firstRoundStart(String gameRoomId, GameRoom gameRoom) {
        LocalDateTime now = LocalDateTime.now();

        //todo: timer 추후 변경 필요
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                applicationEventPublisher.publishEvent(new RoundOverEvent(gameRoomId,GameStatus.FIRST_ROUND));
            }
        };

        gameRoom.startFirstRound(now);
        gameRoomRepository.save(gameRoom);
        timer.schedule(timerTask,ROUND_TIME);

        applicationEventPublisher.publishEvent(new FirstRoundPreparedEvent(gameRoom.getMemberOne().memberId(), gameRoom.getMemberTwo().memberId()));
    }


}
