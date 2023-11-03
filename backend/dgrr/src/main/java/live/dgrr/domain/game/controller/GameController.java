package live.dgrr.domain.game.controller;

import live.dgrr.domain.game.dto.GameResultResponse;
import live.dgrr.domain.game.dto.GameStartResponse;
import live.dgrr.domain.game.entity.event.FirstRoundEndEvent;
import live.dgrr.domain.game.entity.event.FirstRoundPreparedEvent;
import live.dgrr.domain.game.entity.event.SecondRoundEndEvent;
import live.dgrr.domain.game.entity.event.SecondRoundPreparedEvent;
import live.dgrr.domain.game.service.GameFirstRoundService;
import live.dgrr.domain.game.service.GameSecondRoundService;
import live.dgrr.domain.waitingroom.entity.GameStartEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final GameFirstRoundService gameFirstRoundService;
    private final GameSecondRoundService gameSecondRoundService;
    private final SimpMessagingTemplate template;

    private static final String GAME_START_DEST = "/recv/game-start";
    private static final String FIRST_ROUND_START = "/recv/firstroundstart";
    private static final String SECOND_ROUND_START = "/recv/secondroundstart";
    private static final String GAME_RESULT_DEST = "/recv/game-result";
    private static final String ENEMY_LEAVE_GAME = "/recv/enemy-left";
    @EventListener
    public void gameStart(GameStartEvent event) {
        List<GameStartResponse> gameStartResponses = gameFirstRoundService.gameStart(event.memberOneId(), event.memberTwoId(), event.gameType());

        template.convertAndSendToUser(gameStartResponses.get(0).myInfo().memberId(),GAME_START_DEST, gameStartResponses.get(0));
        template.convertAndSendToUser(gameStartResponses.get(1).myInfo().memberId(),GAME_START_DEST, gameStartResponses.get(1));
    }

    @MessageMapping("/firstroundstart")
    public void firstRoundStart(@Payload String gameRoomId) {
        gameFirstRoundService.prepareFirstRoundStart(gameRoomId);
    }

    @EventListener
    public void firstRoundPrepared(FirstRoundPreparedEvent event) {
        template.convertAndSendToUser(event.memberOneId(), FIRST_ROUND_START, "START");
        template.convertAndSendToUser(event.memberTwoId(), FIRST_ROUND_START, "START");
    }

    @EventListener
    public void firstRoundEnd(FirstRoundEndEvent event) {
        template.convertAndSendToUser(event.memberOneId(), event.destination(), event.roundResult());
        template.convertAndSendToUser(event.memberTwoId(), event.destination(), event.roundResult());
    }

    @MessageMapping("/secondroundstart")
    public void secondRoundStart(@Payload String gameRoomId) {
        gameSecondRoundService.prepareSecondRoundStart(gameRoomId);
    }

    @EventListener
    public void secondRoundPrepared(SecondRoundPreparedEvent event) {
        template.convertAndSendToUser(event.memberOneId(), SECOND_ROUND_START, "START");
        template.convertAndSendToUser(event.memberTwoId(), SECOND_ROUND_START, "START");
    }

    @EventListener
    public void secondRoundEnd(SecondRoundEndEvent event) {
        template.convertAndSendToUser(event.memberOneId(), event.destination(), event.roundResult());
        template.convertAndSendToUser(event.memberTwoId(), event.destination(), event.roundResult());
    }

    @MessageMapping("/game-end")
    public void gameResult(Principal principal, @Payload String gameRoomId) {
        GameResultResponse result = gameSecondRoundService.gameResult(principal.getName(), gameRoomId);
        template.convertAndSendToUser(principal.getName(),GAME_RESULT_DEST,result);
    }

    @MessageMapping("/game-leave")
    public void leaveGame(Principal principal, @Payload String gameRoomId) {
        GameResultResponse result = gameSecondRoundService.leaveGame(principal.getName(), gameRoomId);
        template.convertAndSendToUser(result.myInfo().memberId(),ENEMY_LEAVE_GAME,result);
    }
}
