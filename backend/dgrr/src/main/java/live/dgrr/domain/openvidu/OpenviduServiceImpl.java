package live.dgrr.domain.openvidu;

import org.springframework.stereotype.Service;

@Service
public class OpenviduServiceImpl implements OpenviduService{
    @Override
    public String createSession(String gameRoomId) {
        return "session";
    }

    @Override
    public String createConnection(String sessionId) {
        return "token";
    }
}
