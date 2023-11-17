package live.dgrr.domain.openvidu;

import org.springframework.stereotype.Service;

@Service
public interface OpenviduService {
    String createSession(String gameRoomId);
    String createConnection(String sessionId);
}
