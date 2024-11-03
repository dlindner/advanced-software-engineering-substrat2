package io.nyando.factorix.services.interfaces;

import io.nyando.factorix.services.events.Event;

public interface EventBroker {

    void post(Event event);

    void register(Object object, Class<? extends Event> event);

}
