package live.dgrr.domain.gamehistory.service;

import live.dgrr.domain.game.entity.GameResult;
import live.dgrr.domain.game.entity.GameRoom;
import live.dgrr.domain.gamehistory.entity.GameHistory;
import live.dgrr.domain.gamehistory.repository.GameHistoryRepository;
import live.dgrr.domain.member.entity.Member;
import live.dgrr.domain.member.repository.MemberRepository;
import live.dgrr.global.exception.ErrorCode;
import live.dgrr.global.exception.GameException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameHistoryService {

    private final GameHistoryRepository gameHistoryRepository;
    private final MemberRepository memberRepository;

    public void save(GameRoom gameRoom, String gameRoomId, String memberId, GameResult gameResult, int ratingChange, String highlightImage) {
        Member member = memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new GameException(ErrorCode.MEMBER_NOT_FOUND));

        GameHistory history = GameHistory.builder()
                .member(member)
                .gameRoomId(gameRoomId)
                .gameResult(gameResult)
                .gameType(gameRoom.getGameType())
                .gameTime(gameRoom.getGameTime())
                .ratingChange(ratingChange)
                .holdingTime(gameRoom.getHoldingTime(memberId))
                .highlightImage(highlightImage)
                .build();
        gameHistoryRepository.save(history);
    }

    public void leaveSave(GameRoom gameRoom, String gameRoomId, String memberId, GameResult gameResult, int ratingChange) {
        Member member = memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new GameException(ErrorCode.MEMBER_NOT_FOUND));

        GameHistory history = GameHistory.builder()
                .member(member)
                .gameRoomId(gameRoomId)
                .gameResult(gameResult)
                .gameType(gameRoom.getGameType())
                .ratingChange(ratingChange)
                .build();
        gameHistoryRepository.save(history);
    }
}
