package io.nyando.factorix.model.work;

import java.util.List;
import java.util.Optional;

/**
 * Functions that the workplace's task queue must provide.
 * The queue's order is (by default) defined by the order in which elements are added.
 * However, elements may be reordered by calling the {@see move()} method.
 * @param <T> Type of queued element.
 */
public interface PrioQueue<T> {

    /**
     * Move the element at sourceIndex to destIndex without changing the ordering of the rest of the queue.
     * @param sourceIndex Index of element to move.
     * @param destIndex Destination index of moved element.
     */
    void move(int sourceIndex, int destIndex);

    /**
     * Add a new element to the queue.
     * @param t Element to add.
     */
    void add(T t);

    /**
     * Remove an element from the queue.
     * @param t Element to remove.
     * @return True if an element was removed, false otherwise.
     */
    boolean remove(T t);

    /**
     * Look at the head of the queue without removing it.
     * @return Optional containing the head of the queue if the queue is not empty, empty Optional otherwise.
     */
    Optional<T> peek();

    /**
     * Remove and return the head of the queue.
     * @return Head of the queue.
     */
    T poll();

    /**
     * Return a list of product IDs in the queue.
     * @return List of product IDs.
     */
    List<String> tasks();

}
