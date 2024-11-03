package io.nyando.factorix.config;

import io.nyando.factorix.model.work.ProductionTimeSupplier;

import java.util.Map;

public class WorkplaceTimeSupplier implements ProductionTimeSupplier {

    private final Map<String, Integer> productTimes;

    public WorkplaceTimeSupplier(Map<String, Integer> productTimes) {
        this.productTimes = productTimes;
    }

    @Override
    public int getProductionTime(String productType) {
        return this.productTimes.get(productType);
    }
}
