package live.dgrr.domain.watingroom.entity;

import live.dgrr.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "waitingroom")
public class WaitingRoom extends BaseEntity {
    @Id
    private int roomId;
    private WaitingMember memberOne;
    private WaitingMember memberTwo;
    private List<WaitingMember> watchers;

    public WaitingRoom(int waitingRoomId) {
        this.roomId = waitingRoomId;
    }
}
