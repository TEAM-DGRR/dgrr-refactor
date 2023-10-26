package live.dgrr.domain.watingroom.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "waitingroom", timeToLive = 600)
public class WaitingRoom {
    @Id
    private int roomId;
    private List<WaitingMember> waitingMemberList;
    private List<WaitingMember> watchers;


    public WaitingRoom(int waitingRoomId) {
        this.roomId = waitingRoomId;
    }

    public void addMember(WaitingMember waitingMember) {
        if(this.waitingMemberList == null) {
            this.waitingMemberList = new ArrayList<>();
        }
        this.waitingMemberList.add(waitingMember);
    }
}
