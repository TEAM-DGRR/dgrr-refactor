package live.dgrr.domain.game.service;


import live.dgrr.domain.game.dto.GameStartResponse;
import live.dgrr.domain.game.repository.GameRoomRepository;
import live.dgrr.domain.openvidu.OpenviduService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GameFirstRoundServiceTest {

    @Mock
    OpenviduService openviduService;
    @Mock
    GameRoomRepository gameRoomRepository;

    @InjectMocks
    GameFirstRoundService gameFirstRoundService;
    @Test
    void gameStartTest() {
        String memberOneId = "oneId";
        String memberTwoId = "twoId";
        List<GameStartResponse> gameStartResponses = gameFirstRoundService.gameStart(memberOneId, memberTwoId);
        assertThat(gameStartResponses.get(0).myInfo().memberId()).isEqualTo(memberOneId);
        assertThat(gameStartResponses.get(1).myInfo().memberId()).isEqualTo(memberTwoId);
    }
}