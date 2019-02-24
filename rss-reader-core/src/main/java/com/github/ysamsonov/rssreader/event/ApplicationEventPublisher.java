package com.github.ysamsonov.rssreader.event;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
public interface ApplicationEventPublisher {

    void publish(ApplicationEvent event);

    interface ApplicationEventListener<T extends ApplicationEvent> {
        void onEvent(T event);
    }

    interface ApplicationEvent {
    }
}
