package io.nyando.factorix.model.work;

import io.nyando.factorix.model.order.Product;

import java.util.Collection;
import java.util.Collections;

/**
 * Represents a process running on a workplace.
 * A process has two possible states:
 *   - working on a product: currentProduct is a singleton collection containing the production unit.
 *   - idle: currentProduct is an empty collection.
 */
public class ProductionProcess implements Process {

    private final String processType;

    private ProductionTimeSupplier timeSupplier;

    private Collection<Product> currentProduct;
    private long completionTimer;

    public ProductionProcess(String processType) {
        this.processType = processType;
        this.currentProduct = Collections.emptyList();
        this.completionTimer = 0;
    }

    /**
     * Production time depends on both workplace and type of product.
     * A corresponding object supplying this data over the ProductionTimeSupplier interface is set here.
     * @param supplier Object supplying the production time given this workplace and a product type.
     */
    @Override
    public void setTimeSupplier(ProductionTimeSupplier supplier) {
        this.timeSupplier = supplier;
    }

    @Override
    public String type() {
        return this.processType;
    }

    @Override
    public boolean idle() {
        return this.currentProduct.isEmpty();
    }

    @Override
    public boolean completed() {
        return this.completionTimer == 0;
    }

    @Override
    public String currentProductID() {
        if (this.idle()) {
            return "";
        } else {
            return this.currentProduct.iterator().next().getProductID();
        }
    }

    @Override
    public void startProcess(Product product) {
        this.currentProduct = Collections.singleton(product);
        this.completionTimer = this.timeSupplier.getProductionTime(product.getProductType());
    }

    @Override
    public void time(long currentTime) {
        // If the process isn't running, do nothing.
        if (this.idle()) { return; }
        // If a product is being processed, count down the completion timer.
        this.completionTimer--;
    }

    @Override
    public void completeProcess(long currentTime) {
        this.currentProduct.iterator().next().completeProcess(this.type());
    }

    @Override
    public Product retrieveCompletedProduct() {
        Product product = this.currentProduct.iterator().next();
        this.currentProduct = Collections.emptyList();
        return product;
    }
}
