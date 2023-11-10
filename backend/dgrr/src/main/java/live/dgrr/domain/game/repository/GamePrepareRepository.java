package live.dgrr.domain.game.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GamePrepareRepository {

    private final RedisTemplate<String, Long> redisTemplate;

    public Long prepareRoundOne(String gameRoomId) {
        ValueOperations<String, Long> valueOps = redisTemplate.opsForValue();
        return valueOps.increment("gameRoomId:"+gameRoomId+":roundOne");
    }

    public Long prepareRoundTwo(String gameRoomId) {
        ValueOperations<String, Long> valueOps = redisTemplate.opsForValue();
        return valueOps.increment("gameRoomId:"+gameRoomId+":roundTwo");
    }

    public Boolean deletePrepareRoundOne(String gameRoomId) {
        return redisTemplate.delete("gameRoomId:" + gameRoomId + ":roundOne");
    }

    public Boolean deletePrepareRoundTwo(String gameRoomId) {
        return redisTemplate.delete("gameRoomId:" + gameRoomId + ":roundTwo");
    }
}
