package live.dgrr.domain.matching.service;

import live.dgrr.domain.game.entity.event.GameType;
import live.dgrr.domain.matching.repository.MatchingRepository;
import live.dgrr.domain.waitingroom.entity.GameStartEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final MatchingRepository matchingRepository;
    private final ApplicationEventPublisher publisher;

    public void matchRandom(String memberId) {
        //TODO: 트랜잭션 처리 및 동시성 처리
        log.info("MatchingService - matchRandom : isMemberSetEmpty : {}", matchingRepository.isMemberSetEmpty());

        if(!matchingRepository.isMemberSetEmpty() && !matchingRepository.isFirstMember(memberId)) {
            //member가 있고 제일 첫번째가 본인이 아니면 매칭해서 보내기
            publisher.publishEvent(new GameStartEvent(matchingRepository.publishFirstMember(),memberId, GameType.RANDOM));
        }else {
            //사용자 넣기
            matchingRepository.addMember(memberId, (double) System.currentTimeMillis());
        }

    }

    public void matchingCancel(String memberId) {
        matchingRepository.removeMember(memberId);
    }
}
