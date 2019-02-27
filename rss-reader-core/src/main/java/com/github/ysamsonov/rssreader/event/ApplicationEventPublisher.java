package com.github.ysamsonov.rssreader.event;

/**
 * Interface that encapsulates event publication functionality.
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-24
 */
public interface ApplicationEventPublisher {

    /**
     * Notify all <strong>matching</strong> listeners registered with this application of an event.
     *
     * @param event the event to publish
     */
    void publish(ApplicationEvent event);

    /**
     * Interface to be implemented by application event listeners.
     *
     * @param <T>the specific ApplicationEvent subclass to listen to
     */
    interface ApplicationEventListener<T extends ApplicationEvent> {
        void onEvent(T event);
    }

    /**
     * Interface to be implemented by all application events
     */
    interface ApplicationEvent {
    }
}
