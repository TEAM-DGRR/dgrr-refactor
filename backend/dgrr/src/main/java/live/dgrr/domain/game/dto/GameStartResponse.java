package live.dgrr.domain.game.dto;

import live.dgrr.domain.game.entity.GameMember;

public record GameStartResponse(GameMember myInfo, GameMember enemyInfo, String gameRoomId, String openviduToken,
                                String turn) {
}
