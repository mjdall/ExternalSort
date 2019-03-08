package Heap;

// TODO: Do I need to handle error conditions?

public class Heap <T extends Comparable<T>> {
    private int heapSize;
    private T[] heap;
    private Comparer comparer;

    public Heap (T[] rawArray, Comparer comparisonFunction) {
        this.comparer = comparisonFunction;
        this.heap = rawArray;
        this.heapSize = rawArray.length;
    }

    private int getParentIndex (int index) { return (index - 1) / 2; }

    private int getLeftChildIndex (int index) { return index * 2 + 1; }

    private int getRightChildIndex (int index) { return index * 2 + 2; }

    private void swapHeadAndTail () {
        // error check?
        T head = heap[0];
        heap[0] = heap[heapSize - 1];
        heap[heapSize - 1] = head;
    }

    /**
     *
     */
    private int trySwapUp (int nodeIndex) {
        if (nodeIndex <= 0) { return -1; }

        int parentIndex = getParentIndex(nodeIndex);
        T currentItem = heap[nodeIndex];
        T parentItem = heap[parentIndex];
        if (comparer.compare(parentItem, currentItem) > 0) { return -1; }

        // if the currentItem has a higher priority than it's parent
        heap[parentIndex] = currentItem;
        heap[nodeIndex] = parentItem;
        return parentIndex;
    }

    private int trySwapDown (int nodeIndex) {
        // if it's at the bottom or can't be moved
        if (nodeIndex == heapSize - 1 || nodeIndex < 0) { return -1; }
        int lIdx = getLeftChildIndex(nodeIndex);
        int rIdx = getRightChildIndex(nodeIndex);

        T currNode = heap[nodeIndex];
        T leftChild = heap[lIdx];
        T rightChild = heap[rIdx];

        int leftComparison = comparer.compare(currNode, leftChild);

        // get the smallest of the comparison values
        int smallest = Math.min(leftComparison, comparer.compare(currNode, rightChild));

        // if smallest < 0 then the current item has less priority
        if (smallest >= 0) { return -1; }

        // swap the values
        int modifiedIndex = smallest == leftComparison ? lIdx : rIdx;
        heap[nodeIndex] = heap[modifiedIndex];
        heap[modifiedIndex] = currNode;

        return modifiedIndex;
    }

    public T remove () {
        if (heapSize == 0) { return null; }
        swapHeadAndTail();
        int insIndex = 0;
        while (insIndex > 0) { insIndex = trySwapDown(insIndex); }
        heapSize--;
        return heap[heapSize];
    }

    public T replace (T newItem) {
        return newItem;
    }

    public boolean insert (T newItem) {
        if (heapSize == heap.length) { return false; }
        int insIndex = heapSize;
        heapSize++;
        heap[insIndex] = newItem;

        while (insIndex > 0) { insIndex = trySwapUp(insIndex); }
        return true;
    }

    private void heapify () {
        // assume everything is unsorted, put it in order
    }

}
