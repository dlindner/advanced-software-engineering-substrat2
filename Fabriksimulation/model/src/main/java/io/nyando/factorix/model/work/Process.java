package io.nyando.factorix.model.work;

import io.nyando.factorix.model.order.Product;
import io.nyando.factorix.model.time.TimerListener;

public interface Process extends TimerListener {

    void setTimeSupplier(ProductionTimeSupplier supplier);
    String type();
    boolean idle();
    boolean completed();
    String currentProductID();
    void startProcess(Product product);
    void completeProcess(long currentTime);
    Product retrieveCompletedProduct();

}
