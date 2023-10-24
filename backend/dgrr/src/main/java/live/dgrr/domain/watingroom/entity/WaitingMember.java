package live.dgrr.domain.watingroom.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;


@Getter
@RedisHash(value = "waitingmember",timeToLive = 600)
@NoArgsConstructor
@AllArgsConstructor
public class WaitingMember {
    @Id
    private String waitingMemberId;
    private String nickname;
    private String profileImage;
    private boolean isReady;

}
