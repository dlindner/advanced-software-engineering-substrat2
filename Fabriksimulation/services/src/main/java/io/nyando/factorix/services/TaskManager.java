package io.nyando.factorix.services;

import io.nyando.factorix.model.order.Order;
import io.nyando.factorix.model.order.Product;
import io.nyando.factorix.model.work.Task;
import io.nyando.factorix.model.work.Workplace;
import io.nyando.factorix.services.interfaces.SimConfiguration;

import java.util.Collection;

/**
 * Manages creation and distribution of workplace tasks.
 * Given an order, distributes a task for every product to each workplace that can take part in the production chain.
 */
public class TaskManager {

    private final SimConfiguration config;
    private final Collection<Workplace> workplaces;

    public TaskManager(SimConfiguration config, Collection<Workplace> workplaces) {
        this.config = config;
        this.workplaces = workplaces;
    }

    /**
     * Create task instances for an order and distribute them to corresponding workplaces.
     * @param order Order object to create tasks for.
     */
    public void createAndDistributeTasks(Order order) {
        Iterable<String> procSequence = this.config.getProductProcessSequences(order.getProductType()).orElseThrow();

        for (Product product : order.getProducts()) {
            for (String proc : procSequence) {
                Task task = new Task(product.getProductID(), proc);
                this.findByProcess(proc).forEach(wp -> wp.addTask(task));
            }
        }
    }

    private Iterable<Workplace> findByProcess(String processType) {
        return this.workplaces.stream().filter(wp -> wp.getProcessType().equals(processType)).toList();
    }
}
