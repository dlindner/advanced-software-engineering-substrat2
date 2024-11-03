package io.nyando.factorix.model.work;

import io.nyando.factorix.model.order.Product;
import io.nyando.factorix.model.order.StashRetrieve;
import io.nyando.factorix.model.time.TimerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Represents a workplace in the simulation.
 * Each workplace offers one production process.
 * The next product to work on is determined by the first entry in the production task queue.
 * Products are retrieved from the product pool when starting production and stashed back after finishing.
 */
public class Workplace implements TimerListener {

    private final static Logger log = LoggerFactory.getLogger(Workplace.class);

    private final String workplaceID;
    private final Process process;

    private final PrioQueue<Task> tasks;
    private StashRetrieve products;

    public Workplace(String workplaceID, Process process) {
        this.workplaceID = workplaceID;
        this.process = process;
        this.tasks = new TaskQueue();
    }

    public String getWorkplaceID() {
        return this.workplaceID;
    }

    public String getProcessType() {
        return this.process.type();
    }

    public String currentProductID() {
        return this.process.currentProductID();
    }

    public List<String> getTaskQueue() {
        return this.tasks.tasks();
    }

    public void moveTask(int sourceIndex, int destIndex) {
        this.tasks.move(sourceIndex, destIndex);
    }

    /**
     * Set an object that supplies the production time for each product fabricated on this workplace.
     * @param supplier - Object that supplies production times.
     */
    public void setProductionTimeSupplier(ProductionTimeSupplier supplier) {
        this.process.setTimeSupplier(supplier);
    }

    /**
     * Set the product repository to retrieve products from.
     * @param products - Product repository to use for this workplace.
     */
    public void setProductStashRetrieve(StashRetrieve products) {
        this.products = products;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void removeTask(String productID, String processType) {
        this.tasks.remove(new Task(productID, processType));
    }

    private void startNextTaskIfPossible(long currentTime) {
        Optional<Task> nextTask = this.tasks.peek();
        Optional<Product> nextProduct = nextTask.map(this::checkTaskProduct);

        if (nextProduct.isPresent()) {
            if (nextProduct.get().nextProcess().equals(this.process.type())) {
                Optional<Product> product = this.products.retrieve(nextTask.get().productID());
                product.ifPresent(p -> this.startTask(p, currentTime));
            }
        }
    }

    private void startTask(Product product, long currentTime) {
        log.info("{}: Workplace {} starting Product {}",
                currentTime, this.workplaceID, product.getProductID());
        this.tasks.poll();
        this.process.startProcess(product);
    }

    private void completeTask(long currentTime) {
        log.info("{}: Workplace {} completed Product {}",
                currentTime, this.workplaceID, this.currentProductID());
        this.process.completeProcess(currentTime);
        this.products.stash(this.process.retrieveCompletedProduct());
    }

    private Product checkTaskProduct(Task task) {
        return this.products.check(task.productID());
    }

    @Override
    public void time(long currentTime) {
        this.process.time(currentTime);

        if (this.process.idle()) {
            this.startNextTaskIfPossible(currentTime);
        } else if (this.process.completed()) {
            this.completeTask(currentTime);
        }
    }

}
