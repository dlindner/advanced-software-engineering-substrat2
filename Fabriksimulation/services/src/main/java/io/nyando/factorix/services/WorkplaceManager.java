package io.nyando.factorix.services;

import io.nyando.factorix.model.order.StashRetrieve;
import io.nyando.factorix.model.work.Process;
import io.nyando.factorix.model.work.ProductionProcess;
import io.nyando.factorix.model.work.Workplace;
import io.nyando.factorix.services.events.ProcessCompleted;
import io.nyando.factorix.services.events.ProcessCompletedListener;
import io.nyando.factorix.services.events.ProcessEventDecorator;
import io.nyando.factorix.services.interfaces.EventBroker;
import io.nyando.factorix.services.interfaces.SimConfiguration;
import io.nyando.factorix.services.time.Timer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

/**
 * Manages workplace lifecycles.
 * Creates workplaces from a definition (SimConfiguration interface) and registers them at the timer instance.
 * When an order is created, creates tasks and distributes them to workplaces according to their respective process types.
 */
public class WorkplaceManager implements ProcessCompletedListener {

    private final SimConfiguration config;
    private final Collection<Workplace> workplaces;
    private final StashRetrieve productRepository;
    private final Timer timer;

    public WorkplaceManager(SimConfiguration config, StashRetrieve productRepository, Timer timer, EventBroker broker) {
        this.config = config;
        this.workplaces = new HashSet<>();
        this.productRepository = productRepository;
        this.timer = timer;

        this.buildWorkplaces(broker);
        broker.register(this, ProcessCompleted.class);
    }

    private void buildWorkplaces(EventBroker broker) {
        for (var workplaceConf : this.config.getWorkplaceConfiguration().entrySet()) {
            String workplaceID = workplaceConf.getKey();
            String processType = workplaceConf.getValue();

            Process process = new ProcessEventDecorator(workplaceID, new ProductionProcess(processType), broker);
            Workplace workplace = new Workplace(workplaceID, process);

            workplace.setProductionTimeSupplier(this.config.getProductionTimeSupplier(processType, workplaceID));
            workplace.setProductStashRetrieve(this.productRepository);
            this.workplaces.add(workplace);
            this.timer.register(workplace);
        }
    }

    public Collection<Workplace> getWorkplaces() {
        return this.workplaces;
    }

    public Optional<Workplace> getWorkplace(String workplaceID) {
        return this.workplaces.stream()
                .filter(wp -> wp.getWorkplaceID().equals(workplaceID))
                .findFirst();
    }

    @Override
    public void onProcessCompleted(ProcessCompleted event) {
        for (Workplace workplace : this.workplaces) {
            workplace.removeTask(event.productID(), event.processType());
        }
    }
}
