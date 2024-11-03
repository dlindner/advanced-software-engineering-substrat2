package io.nyando.factorix.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.nyando.factorix.services.interfaces.EventBroker;
import io.nyando.factorix.services.events.*;

import java.util.ArrayList;
import java.util.List;

public class EventProxy implements EventBroker, OrderCreatedListener, ProcessStartedListener, ProcessCompletedListener {

    private final EventBus eventBus;

    private final List<OrderCreatedListener> orderCreatedListeners;
    private final List<ProcessStartedListener> processStartedListeners;
    private final List<ProcessCompletedListener> processCompletedListeners;

    public EventProxy() {
        this.eventBus = new EventBus();
        this.orderCreatedListeners = new ArrayList<>();
        this.processStartedListeners = new ArrayList<>();
        this.processCompletedListeners = new ArrayList<>();
        this.eventBus.register(this);
    }

    @Override
    @Subscribe
    public void onOrderCreated(OrderCreated event) {
        this.orderCreatedListeners.forEach(sub -> sub.onOrderCreated(event));
    }

    @Override
    @Subscribe
    public void onProcessStarted(ProcessStarted event) {
        this.processStartedListeners.forEach(sub -> sub.onProcessStarted(event));
    }

    @Override
    @Subscribe
    public void onProcessCompleted(ProcessCompleted event) {
        this.processCompletedListeners.forEach(sub -> sub.onProcessCompleted(event));
    }

    @Override
    public void post(Event event) {
        this.eventBus.post(event);
    }

    @Override
    public void register(Object object, Class<? extends Event> event) {
        if (event.equals(OrderCreated.class)) {
            this.orderCreatedListeners.add((OrderCreatedListener) object);
        } else if (event.equals(ProcessStarted.class)) {
            this.processStartedListeners.add((ProcessStartedListener) object);
        } else if (event.equals(ProcessCompleted.class)) {
            this.processCompletedListeners.add((ProcessCompletedListener) object);
        }
    }
}
