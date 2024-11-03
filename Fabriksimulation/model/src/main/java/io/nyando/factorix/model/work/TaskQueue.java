package io.nyando.factorix.model.work;

import java.util.*;
import java.util.stream.Collectors;

public class TaskQueue implements PrioQueue<Task> {

    private final List<Task> tasks;

    public TaskQueue() {
        this.tasks = new LinkedList<>();
    }

    @Override
    public void move(int sourceIndex, int destIndex) {
        // Move the element at sourceIndex to the new index destIndex in the list.
        // See also: https://docs.oracle.com/javase/6/docs/api/java/util/Collections.html - static method "rotate"
        if (sourceIndex < destIndex) {
            Collections.rotate(tasks.subList(sourceIndex, destIndex + 1), -1);
        } else if (sourceIndex > destIndex) {
            Collections.rotate(tasks.subList(destIndex, sourceIndex + 1), 1);
        }
    }

    @Override
    public void add(Task task) {
        this.tasks.add(task);
    }

    @Override
    public boolean remove(Task task) {
        return this.tasks.remove(task);
    }

    @Override
    public Optional<Task> peek() {
        return !this.tasks.isEmpty() ? Optional.of(this.tasks.get(0)) : Optional.empty();
    }

    @Override
    public Task poll() {
        return this.tasks.remove(0);
    }

    @Override
    public List<String> tasks() {
        return this.tasks.stream().map(Task::productID).collect(Collectors.toList());
    }

}
