package Heap;

public interface Comparer <T> {
    /**
     * Comparison Function used for the Heap.
     * @param item1 - The item you are comparing with.
     * @param item2 - The item you compare in relation to item1.
     * @return Integer representing the priority difference of the two items.
     *  Larger than 0 indicates that item1 is of higher priority than item2.
     */
    int compare(T item1, T item2);
}
