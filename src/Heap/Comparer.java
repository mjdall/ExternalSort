package Heap;

public interface Comparer <T> {
    /**
     * Comparison Function used for the Heap.
     * @param item1 - The item you are comparing with.
     * @param item2 - The item you compare in relation to item1.
     * @return Integer representing the priority difference of the two items.
     * Less than 0 means that item1 comes before item2. 0 means items are equal. >0 means item1 comes after item2
     */
    int compare(T item1, T item2);
}
