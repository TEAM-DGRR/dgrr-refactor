package live.dgrr.domain.game;

import live.dgrr.domain.game.entity.GameMember;

public record GameStartDto(GameMember myInfo, GameMember enemyInfo, String gameRoomId, String openviduToken,
                           String turn) {
}
