package live.dgrr.domain.matching.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MatchingRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String WAITING_MEMBERS_KEY = "waitingQueue";


    public void addMember(String memberId, Double currentTimeMillis) {
        redisTemplate.opsForZSet().add(WAITING_MEMBERS_KEY, memberId, currentTimeMillis);
    }

    public boolean isMemberSetEmpty() {
        Long size = redisTemplate.opsForZSet().size(WAITING_MEMBERS_KEY);
        return (size == null || size == 0);
    }

    public boolean isFirstMember(String memberId) {
        Set<String> members = redisTemplate.opsForZSet().range(WAITING_MEMBERS_KEY, 0, 0);
        if (members != null && !members.isEmpty()) {
            return members.iterator().next().equals(memberId);
        }
        return false;
    }

    public String publishFirstMember() {
        Set<String> members = redisTemplate.opsForZSet().range(WAITING_MEMBERS_KEY, 0, 0);
        String memberId = members.iterator().next();
        redisTemplate.opsForZSet().remove(WAITING_MEMBERS_KEY, memberId);
        return memberId;
    }

    public void removeMember(String memberId) {
        redisTemplate.opsForZSet().remove(WAITING_MEMBERS_KEY, memberId);
    }
}
