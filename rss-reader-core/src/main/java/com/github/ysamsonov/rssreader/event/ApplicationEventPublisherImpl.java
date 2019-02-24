package com.github.ysamsonov.rssreader.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
public class ApplicationEventPublisherImpl implements ApplicationEventPublisher {

    private final Map<Class, Collection<ApplicationEventListener>> subscribers = new HashMap<>();

    public <T extends ApplicationEvent> void subscribe(
        Class<T> eventType,
        ApplicationEventListener<T> applicationEventListener
    ) {
        subscribers
            .computeIfAbsent(eventType, k -> new ArrayList<>())
            .add(applicationEventListener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void publish(ApplicationEvent event) {
        var eventType = event.getClass();
        for (var entry : subscribers.entrySet()) {
            if (eventType.isAssignableFrom(entry.getKey())) {
                for (var listener : entry.getValue()) {
                    listener.onEvent(event);
                }
            }
        }
    }
}
