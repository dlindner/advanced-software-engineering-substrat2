package io.nyando.factorix.services.events;

import io.nyando.factorix.model.order.Product;

public record ProcessStarted(long time, String workplaceID, Product product, String processType) implements Event { }
