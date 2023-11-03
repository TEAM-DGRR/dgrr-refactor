package live.dgrr.domain.game.service;


import live.dgrr.domain.game.entity.GameMember;
import live.dgrr.domain.game.entity.GameRoom;
import live.dgrr.domain.game.entity.GameStatus;
import live.dgrr.domain.game.entity.event.GameType;
import live.dgrr.global.entity.Tier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GameFirstRoundService.class)
public class GameFirstRoundExtensionTest {
    @Autowired
    private GameFirstRoundService gameFirstRoundService;


    String gameRoomId;
    String memberOneId;
    String memberTwoId;

    GameMember memberOne;
    GameMember memberTwo;
    GameRoom gameRoom;

    @BeforeEach
    void setUp() {
        gameRoomId = "roomId";
        memberOneId = "M1";
        memberTwoId = "M2";

        memberOne = new GameMember(memberOneId, "Player1", "jpg", "This is description", 1500, Tier.SILVER);
        memberTwo = new GameMember(memberTwoId, "Player2", "jpg", "This is description", 1200, Tier.BRONZE);
        gameRoom = new GameRoom(gameRoomId, memberOne, memberTwo, GameStatus.BEFORE_START, GameType.RANDOM);
    }
}
