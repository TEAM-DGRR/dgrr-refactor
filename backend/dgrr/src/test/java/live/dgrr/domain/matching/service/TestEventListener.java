package live.dgrr.domain.matching.service;

import live.dgrr.domain.waitingroom.entity.GameStartEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TestEventListener {
    private GameStartEvent lastEvent;
    private int eventCount;
    @EventListener
    public void handleGameStartEvent(GameStartEvent event) {
        lastEvent = event;
        eventCount++;
    }

    public GameStartEvent getLastEvent() {
        return lastEvent;
    }

    public void clearLastEvent() {
        lastEvent = null;
    }

    public int getEventsCount() {
        return this.eventCount;
    }

    public void clearEvents() {
        this.eventCount = 0;
    }


}
