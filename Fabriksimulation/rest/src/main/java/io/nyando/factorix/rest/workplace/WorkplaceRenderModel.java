package io.nyando.factorix.rest.workplace;

public record WorkplaceRenderModel(String workplaceID, String processType,
                                   String currentProduct, String[] queue) { }
