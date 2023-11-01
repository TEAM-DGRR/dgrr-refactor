package live.dgrr.domain.capture.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.dgrr.domain.capture.entity.CaptureResult;
import live.dgrr.domain.capture.entity.event.CaptureResultEvent;
import live.dgrr.domain.game.entity.GameMember;
import live.dgrr.domain.game.entity.GameRoom;
import live.dgrr.domain.game.entity.RoundResult;
import live.dgrr.domain.game.entity.event.FirstRoundOverEvent;
import live.dgrr.domain.game.entity.event.SecondRoundOverEvent;
import live.dgrr.domain.game.repository.GameRoomRepository;
import live.dgrr.global.exception.ErrorCode;
import live.dgrr.global.exception.GameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class CaptureService {
    private final ApplicationEventPublisher publisher;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final GameRoomRepository gameRoomRepository;

    public static final int FIRST_ROUND = 1;
    public static final int SECOND_ROUND = 2;

    public CaptureResult deSerializationCapture(String capture) {
        try {
            CaptureResult captureResult = objectMapper.readValue(capture, CaptureResult.class);
            String gameRoomId = captureResult.getHeader().getGameSessionId();
            GameRoom gameRoom = gameRoomRepository.findById(gameRoomId).orElseThrow(() -> new GameException(ErrorCode.GAME_ROOM_NOT_FOUND));

            log.info(capture);

            if (captureResult.getEncodedImage() != null) {
                publishingLaugh(captureResult);
            }

            String memberOneId = Optional.ofNullable(gameRoom.getMemberOne())
                    .map(GameMember::memberId)
                    .map(String::valueOf) // Long 타입을 String으로 변환
                    .orElseThrow(() -> new GameException(ErrorCode.MEMBER_NOT_FOUND));

            String memberTwoId = Optional.ofNullable(gameRoom.getMemberTwo())
                    .map(GameMember::memberId)
                    .map(String::valueOf) // Long 타입을 String으로 변환
                    .orElseThrow(() -> new GameException(ErrorCode.MEMBER_NOT_FOUND));

            publisher.publishEvent(new CaptureResultEvent(memberOneId, memberTwoId, captureResult));
            return captureResult;

        } catch (JsonProcessingException e) {
            throw new GameException(ErrorCode.IMAGE_PROCESSING_ERROR, e);
        }

    }

    public void publishingLaugh(CaptureResult captureResult) {
        String gameSessionId = captureResult.getHeader().getGameSessionId();
        int round = captureResult.getHeader().getRound();

        if (round == FIRST_ROUND) {
            publisher.publishEvent(new FirstRoundOverEvent(gameSessionId, RoundResult.LAUGH));
        } else if (round == SECOND_ROUND) {
            publisher.publishEvent(new SecondRoundOverEvent(gameSessionId, RoundResult.LAUGH));
        }
    }

}