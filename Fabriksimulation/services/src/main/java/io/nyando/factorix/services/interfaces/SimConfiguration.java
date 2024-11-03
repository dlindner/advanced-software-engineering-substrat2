package io.nyando.factorix.services.interfaces;

import io.nyando.factorix.model.work.ProductionTimeSupplier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Provides information about the factory configuration.
 * Information supplied here determines number and type of workplaces, products, and processes in the factory environment.
 */
public interface SimConfiguration {

    /**
     * Supplies a mapping of workplaces to process types.
     * @return Map with workplaceIDs as keys and corresponding process types as values.
     */
    Map<String, String> getWorkplaceConfiguration();

    /**
     * Supplies the process sequence for a given product type.
     * @param productType Identifier of the product type.
     * @return Sequence of processes corresponding to the given product type.
     */
    Optional<List<String>> getProductProcessSequences(String productType);

    /**
     * ProductionTimeSupplier provides the process time for a given workplace, product type, and process type.
     * @param processType The PTS' process type.
     * @param workplaceID The PTS' workplace identifier.
     * @return ProductionTimeSupplier object corresponding to the input process type and workplace ID.
     */
    ProductionTimeSupplier getProductionTimeSupplier(String processType, String workplaceID);

    /**
     * Supplies possible product types to manufacture.
     * @return Iterable data structure of product type identifiers.
     */
    Iterable<String> getProductTypes();

}
