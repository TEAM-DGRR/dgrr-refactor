package live.dgrr.domain.game.controller;

import live.dgrr.domain.game.GameStartDto;
import live.dgrr.domain.game.entity.event.FirstRoundEndEvent;
import live.dgrr.domain.game.entity.event.FirstRoundPreparedEvent;
import live.dgrr.domain.game.service.GameFirstRoundService;
import live.dgrr.domain.watingroom.entity.GameStartEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final GameFirstRoundService gameFirstRoundService;
    private final SimpMessagingTemplate template;

    private static final String GAME_START_DEST = "/recv/game-start";
    private static final String FIRST_ROUND_START = "/recv/firstroundstart";
    @EventListener
    public void gameStart(GameStartEvent event) {
        List<GameStartDto> gameStartDtos = gameFirstRoundService.gameStart(event.memberOneId(), event.memberTwoId());

        template.convertAndSendToUser(gameStartDtos.get(0).myInfo().memberId(),GAME_START_DEST,gameStartDtos.get(0));
        template.convertAndSendToUser(gameStartDtos.get(1).myInfo().memberId(),GAME_START_DEST,gameStartDtos.get(1));
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
}
