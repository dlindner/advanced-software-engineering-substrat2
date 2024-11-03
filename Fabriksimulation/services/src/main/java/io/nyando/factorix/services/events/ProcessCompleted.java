package io.nyando.factorix.services.events;

public record ProcessCompleted(long time, String workplaceID, String productID, String processType) implements Event { }
