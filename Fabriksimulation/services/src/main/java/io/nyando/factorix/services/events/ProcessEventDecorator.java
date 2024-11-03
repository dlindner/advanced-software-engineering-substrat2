package io.nyando.factorix.services.events;

import io.nyando.factorix.model.order.Product;
import io.nyando.factorix.model.work.Process;
import io.nyando.factorix.model.work.ProductionTimeSupplier;
import io.nyando.factorix.services.interfaces.EventBroker;

public class ProcessEventDecorator implements Process {

    private final String workplaceID;
    private final Process process;
    private final EventBroker broker;
    private long currentTime;

    public ProcessEventDecorator(String workplaceID, Process process, EventBroker broker) {
        this.workplaceID = workplaceID;
        this.process = process;
        this.broker = broker;
    }

    @Override
    public void startProcess(Product product) {
        this.process.startProcess(product);
        this.broker.post(new ProcessStarted(this.currentTime, this.workplaceID, product, this.process.type()));
    }

    @Override
    public void completeProcess(long time) {
        this.process.completeProcess(time);
        this.broker.post(new ProcessCompleted(time, this.workplaceID, this.currentProductID(), this.process.type()));
    }

    @Override
    public Product retrieveCompletedProduct() {
        return this.process.retrieveCompletedProduct();
    }

    @Override
    public void time(long currentTime) {
        this.currentTime = currentTime;
        this.process.time(currentTime);
    }

    @Override
    public void setTimeSupplier(ProductionTimeSupplier supplier) {
        this.process.setTimeSupplier(supplier);
    }

    @Override
    public String type() {
        return this.process.type();
    }

    @Override
    public boolean idle() {
        return this.process.idle();
    }

    @Override
    public boolean completed() {
        return this.process.completed();
    }

    @Override
    public String currentProductID() {
        return this.process.currentProductID();
    }

}
