/**
 * Heap implementation
 *
 * Daniel Stokes - 1331134
 * Morgan Dally - ???????
 */

package Heap;

// Make IntelliJ happy
import Heap.Comparer;

// TODO: Do I need to handle error conditions?

public class Heap <T extends Comparable<T>> {
    private int heapSize;
    private T[] heap;
    private Comparer comparer;

    public Heap (T[] rawArray, Comparer comparisonFunction) {
        // Set members
        this.comparer = comparisonFunction;
        this.heap = rawArray;
        this.heapSize = rawArray.length;

        // Put the array in heap order
        resetHeap();
    }

    /**
     * Gets the index of nodes parent
     * @param index Index of the child
     * @return Index of parent
     */
    private int getParentIndex (int index) { return (index - 1) / 2; }

    /**
     * Gets the index of left child
     * @param index Parent index
     * @return Index of left child
     */
    private int getLeftChildIndex (int index) { return index * 2 + 1; }

    /**
     * Checks index is in range
     * @param index Index to check
     * @return True if in range, false otherwise
     */
    private boolean checkIndex (int index) { return index < heapSize; }

    /**
     * Gets size of the heap
     * @return Size of the heap
     */
    public int getHeapSize () { return heapSize; }

    /**
     * Gets the max possible capacity of the heap
     * @return The max capacity of the heap
     */
    public int getMaxHeapSize () { return heap.length; }

    /**
     * Gets the number of free slots in the heap
     * @return Nummber of free slots in the heap
     */
    public int getFreeNodes () { return heap.length - heapSize; }

    /**
     * Gets the underlying array for the heap
     * @return The underlying array
     */
    public T[] getHeap () { return heap; }

    /**
     * Heapifies array not in heap order
     */
    private void buildHeap () {
        // Get first node that has a child
        int startFrom = getParentIndex(heapSize - 1);
        // Traverse all nodes and put them in heap order
        // Start from last parent node so that higher nodes have sub heaps as children
        for (int i = startFrom; i >= 0; i--) {
            downHeap(i);
        }
    }

    /**
     * Swaps first and last element in the heap
     */
    private void swapHeadAndTail () {
        // Check that the heap has something to swap
        if (heapSize <= 1) { return; }
        // Swap head and tail
        T head = heap[0];
        heap[0] = heap[heapSize - 1];
        heap[heapSize - 1] = head;
    }

    /**
     * Perform upheap operation
     * @param index Index to start at
     */
    private void upHeap( int index ) {
        // Swap up until we find the correct place
        while (index > 0) { index = trySwapUp(index); }
    }

    /**
     * Perform down heap operation
     * @param index Index to start at
     */
    private void downHeap( int index ) {
        // Swap down until we find the correct place
        while (index >= 0) { index = trySwapDown(index); }
    }

    /**
     * Swap node with parent if smaller
     * @param nodeIndex Node to try to swap
     * @return New index of the node or -1 if not swapped
     */
    private int trySwapUp (int nodeIndex) {
        // Check we have a parent to swap with
        if (nodeIndex <= 0) { return -1; }

        // Get parent value
        int parentIndex = getParentIndex(nodeIndex);
        T currentItem = heap[nodeIndex];
        T parentItem = heap[parentIndex];

        // Check if we need to swap
        if (comparer.compare(parentItem, currentItem) >= 0) { return -1; }

        // Swap with parent
        heap[parentIndex] = currentItem;
        heap[nodeIndex] = parentItem;
        return parentIndex;
    }

    /**
     * Swap node with child if larger
     * @param nodeIndex Node to try to swap
     * @return New index of the node or -1 if not swapped
     */
    private int trySwapDown (int nodeIndex) {
        // If it's at the bottom
        if (nodeIndex == heapSize - 1 || nodeIndex < 0) { return -1; }

        // Get child indexes
        int lIdx = getLeftChildIndex(nodeIndex);
        int rIdx = lIdx + 1;

        // Left child is out of range - No children
        if (!checkIndex(lIdx)) { return -1; }

        // Get left child value
        T currNode = heap[nodeIndex];
        T leftChild = heap[lIdx];
        T currCompareNode = leftChild;
        int comparisonIndex = lIdx;

        // Check if we have right child
        if (checkIndex(rIdx)) {
            T rightChild = heap[rIdx];
            // Check if right child is less than left
            if (comparer.compare(leftChild, rightChild) > 0) {
                currCompareNode = rightChild;
                comparisonIndex = rIdx;
            }
        }

        // Check if we need to swap - <= 0 means parent is smaller, dont swap down
        int parentCompare = comparer.compare(currNode, currCompareNode);
        if (parentCompare <= 0) { return -1; }

        // Swap down
        heap[nodeIndex] = heap[comparisonIndex];
        heap[comparisonIndex] = currNode;
        // Return new index
        return comparisonIndex;
    }

    /**
     * Insert element into the heap
     * @param newItem Item to insert
     * @return True if the heap had space for the item
     */
    public boolean insert (T newItem) {
        // Check if we have space
        if (heapSize == heap.length) { return false; }
        // Insert new node
        int insIndex = heapSize;
        heapSize++;
        heap[insIndex] = newItem;

        // Perform upheap operation
        upHeap(insIndex);
        return true;
    }

    /**
     * Remove top element from the heap
     * @param keep If the element should be kept at the end of the array
     * @return The removed element, null if heap is empty
     */
    public T remove (boolean keep) {
        // Check there is something to remove
        if (heapSize == 0) { return null; }

        // Swap with last spot in heap
        swapHeadAndTail();
        // Decrease heap size
        heapSize--;
        // Down heap swapped element to restore heap order
        downHeap(0);

        T removed = heap[heapSize];
        // Set old value to null if not keeping it
        if(!keep) {
            heap[heapSize] = null;
        }

        return removed;
    }

    /**
     * Remove root element from heap and insert new item. Does not insert new item if heap is empty
     * @param newItem New item to insert
     * @return Removed element, null if heap is empty
     */
    public T replace (T newItem) {
        // Check there is something to replace
        if (heapSize == 0) { return null; }
        // Replace element
        T curr = heap[0];
        heap[0] = newItem;
        // Down heap new element to restore heap order
        downHeap(0);
        return curr;
    }

    /**
     * Peek at the element on the top of the heap
     * @return The element on top of the heap
     */
    public T peek () {
        // Check there is element to peek at
        if (heapSize == 0) { return null; }
        // Return element
        return heap[0];
    }

    /**
     * Restores unordered heap array to heap ordering
     * @return The size of the new heap
     */
    public int resetHeap () {
        // Compact all values, skipping nulls
        int shift = 0;
        for(int i = 0; i < heap.length; i++) {
            if(heap[i] == null) {
                shift++;
            } else {
                heap[i-shift] = heap[i];
            }
        }
        // Calculate new heap size
        this.heapSize = heap.length - shift;

        // Rebuild the heap
        buildHeap();

        // Return the size of the heap
        return getHeapSize();
    }
}
