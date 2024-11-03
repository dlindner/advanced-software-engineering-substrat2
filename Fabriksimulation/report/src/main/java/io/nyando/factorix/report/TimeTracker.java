package io.nyando.factorix.report;

import io.nyando.factorix.model.order.Order;
import io.nyando.factorix.model.work.Workplace;
import io.nyando.factorix.services.OrderManager;
import io.nyando.factorix.services.WorkplaceManager;
import io.nyando.factorix.services.events.*;
import io.nyando.factorix.services.interfaces.EventBroker;
import io.nyando.factorix.services.time.Timer;

import java.util.*;
import java.util.stream.Collectors;

public class TimeTracker implements OrderCreatedListener, ProcessStartedListener, ProcessCompletedListener {

    private final Timer timer;
    private final OrderManager orderManager;

    private final Map<String, Integer> workplaceOccupancy;
    private final Map<String, Long> processStartTime;
    private final Map<String, Integer> orderTurnaround;
    private final Map<String, Long> orderStartTime;

    private int completedTaskCount;

    public TimeTracker(Timer timer, WorkplaceManager workplaceManager, OrderManager orderManager, EventBroker broker) {
        this.timer = timer;
        this.orderManager = orderManager;
        this.workplaceOccupancy = new HashMap<>();
        this.processStartTime = new HashMap<>();
        this.orderTurnaround = new HashMap<>();
        this.orderStartTime = new HashMap<>();

        broker.register(this, OrderCreated.class);
        broker.register(this, ProcessStarted.class);
        broker.register(this, ProcessCompleted.class);

        for (Workplace workplace : workplaceManager.getWorkplaces()) {
            this.workplaceOccupancy.put(workplace.getWorkplaceID(), 0);
            this.processStartTime.put(workplace.getWorkplaceID(), 0L);
        }
        this.completedTaskCount = 0;
    }

    public void onProcessStarted(ProcessStarted event) {
        this.processStartTime.put(event.workplaceID(), event.time());
    }

    public void onProcessCompleted(ProcessCompleted event) {
        this.completedTaskCount++;
        this.updateWorkplaceOccupancy(event.workplaceID(), event.time());
        this.updateOrderTurnarounds(event.time());
    }

    public void onOrderCreated(OrderCreated event) {
        this.orderStartTime.put(event.orderID(), event.time());
    }

    private void updateWorkplaceOccupancy(String workplaceID, long time) {
        long processTime = time - this.processStartTime.get(workplaceID);
        int oldOccupancy = this.workplaceOccupancy.get(workplaceID);
        this.workplaceOccupancy.put(workplaceID, oldOccupancy + (int) processTime);
    }

    private void updateOrderTurnarounds(long time) {
        Collection<Order> orders = this.orderManager.getOrders();

        for (Order order : orders) {
            String orderID = order.getOrderID();
            if (order.completed() && !this.orderTurnaround.containsKey(orderID)) {
                long startTime = this.orderStartTime.get(orderID);
                int turnaroundTime = (int) (time - startTime);
                this.orderTurnaround.put(orderID, turnaroundTime);
            }
        }
    }

    public Collection<Order> getOrders() {
        return this.orderManager.getOrders();
    }

    public long getSimulationTime() {
        return this.timer.getCurrentTime();
    }

    public int getCompletedTaskCount() {
        return this.completedTaskCount;
    }

    public Map<String, Double> getWorkplaceOccupancy() {
        Map<String, Double> result = new HashMap<>();

        for (var entry : this.workplaceOccupancy.entrySet()) {
            result.put(entry.getKey(), entry.getValue().doubleValue() / (double) this.timer.getCurrentTime());
        }

        return result;
    }

    public Map<String, Integer> getOrderTurnaround() {
        Collection<String> completedOrderIDs =
                this.orderManager.getOrders().stream()
                        .filter(Order::completed)
                        .map(Order::getOrderID)
                        .collect(Collectors.toList());

        Map<String, Integer> result = new HashMap<>();
        for (String id : completedOrderIDs) {
            result.put(id, this.orderTurnaround.get(id));
        }

        return result;
    }
}
