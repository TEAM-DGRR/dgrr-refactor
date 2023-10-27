package live.dgrr.domain.ranking.repository;

import live.dgrr.domain.ranking.dto.RankingResponse;
import live.dgrr.global.config.redis.RankingConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class RankingRepository {

    private final RedisTemplate<String, Long> redisTemplate;
    private final String key;

    public RankingRepository(RedisTemplate<String, Long> redisTemplate, RankingConfig rankingConfig) {
        this.redisTemplate = redisTemplate;
        this.key = rankingConfig.getKey() + rankingConfig.getSeason();
    }

    public boolean addRanking(Long memberId, double score) {
        return redisTemplate.opsForZSet().add(key, memberId, score);
    }

    public List<RankingResponse> getRankingMember(Long memberId) {
        List<RankingResponse> rankings = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<Long>> results = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
        if (results != null) {
            for (ZSetOperations.TypedTuple<Long> result : results) {
                String value = String.valueOf(result.getValue());
                Double score = result.getScore();
                rankings.add(
                        RankingResponse.of(
                                Long.valueOf(value),
                                score,
                                redisTemplate.opsForZSet().reverseRank(key, Long.valueOf(value))+1
                        )
                );
            }
        }
        return rankings;
    }

    public Double getScoreByMemberId(Long memberId) {
        // 멤버 스코어 조회
        Double score = redisTemplate.opsForZSet().score(key, memberId);
        if (score == null) {
            return null;
        }
        return score;
    }

    public Long getRankByMemberId(Long memberId) {
        // 멤버 랭킹 조회 (0부터 시작하므로 1을 더해줌)
        Long rank = redisTemplate.opsForZSet().reverseRank(key, memberId);
        if (rank == null) {
            return null;
        }
        rank += 1;
        return rank;
    }
}
