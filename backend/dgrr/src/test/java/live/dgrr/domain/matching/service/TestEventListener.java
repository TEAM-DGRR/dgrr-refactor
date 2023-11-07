package live.dgrr.domain.matching.service;

import live.dgrr.domain.waitingroom.entity.GameStartEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class TestEventListener {
    private GameStartEvent lastEvent;
    private ConcurrentLinkedQueue<GameStartEvent> events = new ConcurrentLinkedQueue<>();

    @EventListener
    public void handleGameStartEvent(GameStartEvent event) {
        lastEvent = event;
        events.add(event);
    }

    public GameStartEvent getLastEvent() {
        return lastEvent;
    }

    public void clearLastEvent() {
        lastEvent = null;
    }

    public int getEventsCount() {
        return events.size();
    }

    public void clearEvents() {
        events.clear();
    }


}
