package live.dgrr.domain.watingroom.service;

import live.dgrr.domain.watingroom.entity.WaitingRoom;
import live.dgrr.domain.watingroom.repository.WaitingRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingRoomService {

    private final WaitingRoomRepository waitingRoomRepository;

    public WaitingRoom findWaitingRoomById(int roomId) {
        return waitingRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException());
    }

    public int createWaitingRoom() {
        //TODO: waitingRoomId 확인 로직 수정 필요
        int waitingRoomId;
        do {
            waitingRoomId = generateRoomId();
        } while (isWaitingRoomIdExists(waitingRoomId));

        WaitingRoom waitingRoom = waitingRoomRepository.save(new WaitingRoom(waitingRoomId));
        return  waitingRoom.getRoomId();
    }

    public static int generateRoomId() {
        return ThreadLocalRandom.current().nextInt(100000, 1000000);
    }

    public boolean isWaitingRoomIdExists(int waitingRoomId) {
        return waitingRoomRepository.existsById(waitingRoomId);
    }
}
