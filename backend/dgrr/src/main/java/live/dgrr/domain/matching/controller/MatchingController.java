package live.dgrr.domain.matching.controller;

import live.dgrr.domain.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @MessageMapping("/matching")
    public void randomMatching(Principal principal) {
        matchingService.matchRandom(principal.getName());
    }

    @MessageMapping("/matching-cancel")
    public void matchingCancel(Principal principal) {
        matchingService.matchingCancel(principal.getName());
    }

}
