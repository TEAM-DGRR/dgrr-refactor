package live.dgrr.domain.ranking.repository;

import live.dgrr.domain.member.entity.Member;
import live.dgrr.domain.member.repository.MemberRepository;
import live.dgrr.domain.ranking.dto.response.RankingResponse;
import live.dgrr.domain.ranking.entity.Season;
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
    private final String lastKey;
    private final int CURRENT_SEASON;
    private final MemberRepository memberRepository;

    public RankingRepository(RedisTemplate<String, Long> redisTemplate, RankingConfig rankingConfig, MemberRepository memberRepository) {
        this.redisTemplate = redisTemplate;
        this.key = rankingConfig.getKey() + rankingConfig.getSeason();
        this.lastKey = rankingConfig.getKey() + (rankingConfig.getSeason() - 1);
        this.CURRENT_SEASON = rankingConfig.getSeason();
        this.memberRepository = memberRepository;
    }

    public boolean addRanking(Long memberId, double rating) {
        return redisTemplate.opsForZSet().add(key, memberId, rating);
    }

    public List<RankingResponse> getRankingMember(Member member, Season season) {
        List<RankingResponse> rankings = new ArrayList<>();
        String seasonKey = season == Season.CURRENT? key : lastKey;
        Set<ZSetOperations.TypedTuple<Long>> results = redisTemplate.opsForZSet().reverseRangeWithScores (seasonKey, 0, -1);
        if (results != null) {
            for (ZSetOperations.TypedTuple<Long> result : results) {
                String value = String.valueOf(result.getValue());
                Double rating = result.getScore();
                Member memberForRank = memberRepository.findById(Long.valueOf(value)).get();
                rankings.add(
                        RankingResponse.of(
                                Long.valueOf(value),
                                rating,
                                redisTemplate.opsForZSet().reverseRank(seasonKey, Long.valueOf(value))+1,
                                memberForRank.getNickname(),
                                memberForRank.getProfileImage()
                        )
                );
            }
        }
        return rankings;
    }

    public Double getRatingByMemberId(Long memberId) {
        // 멤버 스코어 조회
        Double rating = redisTemplate.opsForZSet().score(key, memberId);
        if (rating == null) {
            return null;
        }
        return rating;
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

    public int getcurentSeason() {
        return CURRENT_SEASON;
    }
}
