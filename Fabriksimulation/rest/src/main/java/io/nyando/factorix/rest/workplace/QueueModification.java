package io.nyando.factorix.rest.workplace;

public class QueueModification {

    private final int sourceIndex;
    private final int destIndex;

    public QueueModification(int sourceIndex, int destIndex) {
        this.sourceIndex = sourceIndex;
        this.destIndex = destIndex;
    }

    public int sourceIndex() {
        return sourceIndex;
    }

    public int destIndex() {
        return destIndex;
    }
}
