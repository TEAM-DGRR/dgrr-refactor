package live.dgrr.domain.matching.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MatchingRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String WAITING_MEMBERS_KEY = "waitingQueue";
    private static final int MATCHING_RANGE = 150;


    public void addMember(String memberId, Double rating) {
        redisTemplate.opsForZSet().add(WAITING_MEMBERS_KEY, memberId, rating);
    }

    public boolean isMemberSetEmpty() {
        Long size = redisTemplate.opsForZSet().size(WAITING_MEMBERS_KEY);
        return (size == null || size == 0);
    }

    public void removeMember(String memberId) {
        redisTemplate.opsForZSet().remove(WAITING_MEMBERS_KEY, memberId);
    }

    public String findClosestRatingMember(double memberRating) {
        // 정의된 레이팅 범위. +/- 50 내에서 검색
        double minRatingRange = memberRating - MATCHING_RANGE;
        double maxRatingRange = memberRating + MATCHING_RANGE;

        Set<String> closeRatingMembers = redisTemplate.opsForZSet().rangeByScore(WAITING_MEMBERS_KEY, minRatingRange, maxRatingRange);

        String closestMemberId = null;
        double closestDistance = Double.MAX_VALUE;

        // Rating이 가까운 멤버 찾기
        for (String closeMemberId : closeRatingMembers) {
            Double rating = redisTemplate.opsForZSet().score(WAITING_MEMBERS_KEY, closeMemberId);
            if (rating != null) {
                double distance = Math.abs(rating - memberRating);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestMemberId = closeMemberId;
                }
            }
        }

        // Rating이 맞는 멤버가 없다면 바로 null 반환
        if (closestMemberId == null) {
            return null;
        }

        // 트랜잭션 내에서 멤버 삭제
        String finalClosestMemberId = closestMemberId;
        List<Object> txResults = redisTemplate.execute(new SessionCallback<>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi(); // 트랜잭션 시작
                operations.opsForZSet().remove(WAITING_MEMBERS_KEY, finalClosestMemberId); // 멤버 삭제
                return operations.exec(); // 트랜잭션 실행
            }
        });

        // 트랜잭션 실행 결과 확인
        if (txResults.isEmpty()) {
            return null; // 트랜잭션 실패 또는 멤버 삭제 실패
        }

        // 성공적으로 멤버 삭제되었으면 ID 반환
        return closestMemberId;
    }
}
