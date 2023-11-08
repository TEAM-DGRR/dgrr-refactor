package live.dgrr.domain.openvidu;

import io.openvidu.java.client.*;
import jakarta.annotation.PostConstruct;
import live.dgrr.global.exception.ErrorCode;
import live.dgrr.global.exception.GeneralException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OpenviduServiceImpl implements OpenviduService{
    @Value("${openvidu_url}")
    private String OPENVIDU_URL;

    @Value("${openvidu_secret}")
    private String OPENVIDU_SECRET;

    private OpenVidu openvidu;

    @PostConstruct
    public void init() {
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }


    public String createSession(String gameSessionId) {
        Map<String,String> parameterMap = new HashMap<>();
        parameterMap.put("customSessionId", gameSessionId);
        SessionProperties properties = SessionProperties.fromJson(parameterMap).build();
        Session session = null;
        try {
            session = openvidu.createSession(properties);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            throw new GeneralException(ErrorCode.OPENVIDU_SESSION_ERROR,e);
        }
        return session.getSessionId();
    }

    public String createConnection(String sessionId) {
        Session session = openvidu.getActiveSession(sessionId);
        if (session == null) {
            return "false";
        }
        Connection connection = null;
        try {
            connection = session.createConnection();
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            throw new GeneralException(ErrorCode.OPENVIDU_CONNECTION_ERROR, e);
        }
        return connection.getToken();
    }
}
