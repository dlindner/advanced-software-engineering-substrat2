package io.nyando.factorix.services.events;

import io.nyando.factorix.model.order.Order;

public record OrderCreated(long time, String orderID, Order order) implements Event { }
