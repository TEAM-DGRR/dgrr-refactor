package live.dgrr.domain.ranking.service;

import live.dgrr.domain.ranking.dto.response.RankingMemberResponse;
import live.dgrr.domain.ranking.dto.response.RankingResponse;
import live.dgrr.domain.ranking.repository.RankingRepository;
import live.dgrr.global.config.redis.RankingConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RankingService {

    private final String key;
    private final RankingRepository rankingRepository;

    @Autowired
    public RankingService(RankingRepository rankingRepository, RankingConfig rankingConfig) {
        this.rankingRepository = rankingRepository;
        this.key = rankingConfig.getKey() + rankingConfig.getSeason();
    }

    public boolean addRanking(Long memberId, double rating) {
        return rankingRepository.addRanking(memberId, rating);
    }

    public RankingMemberResponse getRankingMember(Long memberId) {
        List<RankingResponse> rankings = rankingRepository.getRankingMember(memberId);

        return RankingMemberResponse.of(getRanking(memberId), rankings);
    }

    public RankingResponse getRanking(Long memberId) {
        Double rating = rankingRepository.getRatingByMemberId(memberId);
        Long rank = rankingRepository.getRankByMemberId(memberId);

        return RankingResponse.of(memberId, rating, rank);
    }
}
