package io.nyando.factorix.model.work;

/**
 * Provides time to spend for a given product type to a workplace's process.
 * Production time is dependent on workplace ID, process type, and product type.
 * However, since workplace ID and process type are determined within each workplace,
 * only product type is necessary to calculate a production time within a ProductionProcess object.
 */
public interface ProductionTimeSupplier {

    /**
     * Provides time taken by a production process given a product type.
     * @param productType Product type to calculate production time for.
     * @return Production time for the given product type.
     */
    int getProductionTime(String productType);

}
