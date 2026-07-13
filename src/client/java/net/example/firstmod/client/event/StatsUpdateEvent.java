package net.example.firstmod.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface StatsUpdateEvent {
    Event<StatsUpdateEvent> EVENT = EventFactory.createArrayBacked(StatsUpdateEvent.class,
        listeners -> (levels, pp) -> {
            for (StatsUpdateEvent listener : listeners) {
                listener.onUpdate(levels, pp);
            }
        });

    void onUpdate(int[] statLevels, int availablePp);
}
