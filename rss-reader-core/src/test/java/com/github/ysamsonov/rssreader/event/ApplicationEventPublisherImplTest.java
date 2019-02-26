package com.github.ysamsonov.rssreader.event;

import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @since 2019-02-26
 */
class ApplicationEventPublisherImplTest {

    private ApplicationEventPublisherImpl eventPublisher;

    private AssertListener<BaseTstEvent> baseListener;

    private AssertListener<InheritedEvent1> inh1Listener;

    private AssertListener<InheritedEvent2> inh2Listener;

    @BeforeEach
    void setUp() {
        eventPublisher = new ApplicationEventPublisherImpl();

        baseListener = new AssertListener<>();
        inh1Listener = new AssertListener<>();
        inh2Listener = new AssertListener<>();

        eventPublisher.subscribe(BaseTstEvent.class, baseListener);
        eventPublisher.subscribe(InheritedEvent1.class, inh1Listener);
        eventPublisher.subscribe(InheritedEvent2.class, inh2Listener);
    }

    @Test
    void publishConcreteEventTst() {
        eventPublisher.publish(new InheritedEvent1());

        assertThat(baseListener.isCalled()).isFalse();
        assertThat(inh1Listener.isCalled()).isTrue();
        assertThat(inh2Listener.isCalled()).isFalse();
    }

    @Test
    void publishBaseEventTest() {
        eventPublisher.publish(new BaseTstEvent());

        assertThat(baseListener.isCalled()).isTrue();
        assertThat(inh1Listener.isCalled()).isTrue();
        assertThat(inh2Listener.isCalled()).isTrue();
    }

    @Test
    void publishUnkEventTest() {
        eventPublisher.publish(new UnkEvent());

        assertThat(baseListener.isCalled()).isFalse();
        assertThat(inh1Listener.isCalled()).isFalse();
        assertThat(inh2Listener.isCalled()).isFalse();
    }

    private static class BaseTstEvent implements ApplicationEventPublisher.ApplicationEvent {
    }

    private static class InheritedEvent1 extends BaseTstEvent {
    }

    private static class InheritedEvent2 extends BaseTstEvent {
    }

    private static class UnkEvent implements ApplicationEventPublisher.ApplicationEvent {
    }

    private static class AssertListener<T extends ApplicationEventPublisher.ApplicationEvent>
        implements ApplicationEventPublisher.ApplicationEventListener<T> {

        @Getter
        private boolean isCalled = false;

        @Override
        public void onEvent(T event) {
            this.isCalled = true;
        }
    }
}