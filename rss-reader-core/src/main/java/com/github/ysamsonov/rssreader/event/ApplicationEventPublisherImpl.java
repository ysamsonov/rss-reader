package com.github.ysamsonov.rssreader.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of {@link ApplicationEventPublisher}
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
public class ApplicationEventPublisherImpl implements ApplicationEventPublisher {

    /**
     * Subscribers grouped by {@link ApplicationEventPublisher.ApplicationEvent}
     */
    private final Map<Class, Collection<ApplicationEventListener>> subscribers = new HashMap<>();

    /**
     * Associate event type with listener and write this association to {@link this#subscribers}
     *
     * @param eventType     - event type for subscription
     * @param eventListener - concrete listener
     */
    public <T extends ApplicationEvent> void subscribe(
        Class<T> eventType,
        ApplicationEventListener<T> eventListener
    ) {
        subscribers
            .computeIfAbsent(eventType, k -> new ArrayList<>())
            .add(eventListener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void publish(ApplicationEvent event) {
        var eventType = event.getClass();
        for (var entry : subscribers.entrySet()) {
            if (!eventType.isAssignableFrom(entry.getKey())) {
                continue;
            }

            for (var listener : entry.getValue()) {
                listener.onEvent(event);
            }
        }
    }
}
