package live.dgrr.domain.matching.service;

import live.dgrr.domain.game.entity.event.GameType;
import live.dgrr.domain.matching.repository.MatchingRepository;
import live.dgrr.domain.ranking.repository.RankingRepository;
import live.dgrr.domain.waitingroom.entity.GameStartEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final MatchingRepository matchingRepository;
    private final RankingRepository rankingRepository;
    private final ApplicationEventPublisher publisher;

    public void matchRandom(String memberId) {
        double memberRating = rankingRepository.getRatingByMemberId(Long.valueOf(memberId));
        if (!matchingRepository.isMemberSetEmpty()) {
            String opponentId = matchingRepository.findClosestRatingMember(memberRating);

            if (opponentId != null && !opponentId.equals(memberId)) {
                // 매칭된 상대가 있다면 게임 시작 이벤트 발행
                publisher.publishEvent(new GameStartEvent(opponentId, memberId, GameType.RANDOM));
                return;
            }
        }
        //매칭 상대가 없으면 대기 등록
        matchingRepository.addMember(memberId, memberRating);

    }

    public void matchingCancel(String memberId) {
        matchingRepository.removeMember(memberId);
    }
}
