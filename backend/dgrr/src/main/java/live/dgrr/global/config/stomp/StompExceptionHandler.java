package live.dgrr.global.config.stomp;

import live.dgrr.global.exception.ErrorResponse;
import live.dgrr.global.exception.GameException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StompExceptionHandler  {

    @MessageExceptionHandler(GameException.class)
    @SendToUser("/recv/error")
    public ErrorResponse handleException(GameException ex) {
        return ErrorResponse.of(ex.getErrorCode());
    }
}
