package live.dgrr.domain.game;

import live.dgrr.domain.game.entity.GameMember;

public record GameStartDto(GameMember memberOne, GameMember memberTwo, String gameSessionId, String openviduToken,
                           String turn) {
}