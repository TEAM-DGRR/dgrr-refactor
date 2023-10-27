package live.dgrr.domain.waitingroom.entity;

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
    private boolean isStart;


    public WaitingRoom(int waitingRoomId) {
        this.roomId = waitingRoomId;
        this.isStart = false;
    }

    public void addMember(WaitingMember waitingMember) {
        if(this.waitingMemberList == null) {
            this.waitingMemberList = new ArrayList<>();
        }
        this.waitingMemberList.add(waitingMember);
    }

    public void gameStart() {
        this.isStart = true;
    }
}
