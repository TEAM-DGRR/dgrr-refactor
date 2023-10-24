package live.dgrr.domain.game.service;


import live.dgrr.domain.game.GameStartDto;
import live.dgrr.domain.game.repository.GameRoomRepository;
import live.dgrr.domain.openvidu.OpenviduService;
import org.assertj.core.api.Assertions;
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
        List<GameStartDto> gameStartDtos = gameFirstRoundService.gameStart(memberOneId, memberTwoId);
        assertThat(gameStartDtos.get(0).myInfo().memberId()).isEqualTo(memberOneId);
        assertThat(gameStartDtos.get(1).myInfo().memberId()).isEqualTo(memberTwoId);
    }
}