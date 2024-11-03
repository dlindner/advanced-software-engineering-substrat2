package io.nyando.factorix.model.order;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A product made within the factory environment.
 * Every product has a unique ID and a process tracer list.
 * Tracers are ordered checklists of all processes the product must complete.
 * A product is complete if all tracer values are "true".
 */
public class Product {

    public static final String PRODUCT_COMPLETED = "DONE";

    private final String productID;
    private final String productType;

    private final SortedMap<String, Boolean> tracer;

    public Product(String productID,
                   String productType,
                   Iterable<String> processSequence) {
        this.productID = productID;
        this.productType = productType;
        this.tracer = new TreeMap<>(createProcessOrdering(processSequence));

        for (String proc : processSequence) {
            this.tracer.put(proc, false);
        }
    }

    /**
     * Get the next process the product must complete.
     * @return ID of first incomplete process in the product's process tracer list.
     */
    public String nextProcess() {
        for (var proc : this.tracer.entrySet()) {
            if (!proc.getValue()) {
                return proc.getKey();
            }
        }
        return PRODUCT_COMPLETED;
    }

    /**
     * Mark a process as completed in the process tracer list.
     * @param processID - Name of process to mark as completed.
     */
    public void completeProcess(String processID) {
        this.tracer.put(processID, true);
    }

    public String getProductID() {
        return this.productID;
    }

    public String getProductType() {
        return productType;
    }

    /**
     * Product is completed when all processes are completed.
     * @return True if the product is complete, false otherwise.
     */
    public boolean completed() {
        return !this.tracer.containsValue(false);
    }

    /**
     * Create a comparator to impose an ordering on the process sequence.
     * The order of processes is as they appear in the process sequence list.
     * @param processes Sequence of processes the product must go through.
     * @return Comparator imposing the ordering contained in processes.
     */
    private static Comparator<String> createProcessOrdering(Iterable<String> processes) {
        return (o1, o2) -> {
            if (o1.equals(o2)) { return 0; }
            for (String procName : processes) {
                if (procName.equals(o1)) {
                    return -1;
                } else if (procName.equals(o2)) {
                    return 1;
                }
            }
            throw new IllegalArgumentException("Process IDs not in process list.");
        };
    }

}
