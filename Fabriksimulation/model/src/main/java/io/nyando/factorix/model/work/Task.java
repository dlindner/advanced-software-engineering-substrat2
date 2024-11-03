package io.nyando.factorix.model.work;

/**
 * Workplaces consume tasks as they perform processes on products.
 * Tasks link the ID of a product to the process the workplace must perform on them.
 */
public record Task(String productID, String processType) { }