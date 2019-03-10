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
        buildHeap();
    }

    private int getParentIndex (int index) { return (index - 1) / 2; }

    private int getLeftChildIndex (int index) { return index * 2 + 1; }

    private boolean checkIndex (int index) { return index < heapSize; }

    public int getHeapSize () { return heapSize; }

    public T[] getHeap () { return heap; }

    private void buildHeap () {
        int startFrom = heapSize / 2 - 1;
        for (int i = startFrom; i >= 0; i--) {
            heapify(i);
        }
    }

    private void heapify (int fromIndex) {
        T curr = heap[fromIndex];
        int lIdx = getLeftChildIndex(fromIndex);
        int rIdx = lIdx + 1;

        if (!checkIndex(lIdx)) { return; }

        T leftChild = heap[lIdx];
        int leftComparison = comparer.compare(curr, leftChild);
        int largest = leftComparison;

        if (checkIndex(rIdx)) {
            T rightChild = heap[rIdx];
            largest = Math.max(leftComparison, comparer.compare(curr, rightChild));
        }

        if (largest <= 0) { return; }

        int modifiedIndex = largest == leftComparison ? lIdx : rIdx;
        heap[fromIndex] = heap[modifiedIndex];
        heap[modifiedIndex] = curr;
        heapify(modifiedIndex);
    }

    private void swapHeadAndTail () {
        // error check?
        T head = heap[0];
        heap[0] = heap[heapSize - 1];
        heap[heapSize - 1] = head;
    }

    private int trySwapUp (int nodeIndex) {
        if (nodeIndex <= 0) { return -1; }

        int parentIndex = getParentIndex(nodeIndex);
        T currentItem = heap[nodeIndex];
        T parentItem = heap[parentIndex];

        // if the currentItem has a higher priority than it's parent
        if (comparer.compare(parentItem, currentItem) <= 0) { return -1; }

        heap[parentIndex] = currentItem;
        heap[nodeIndex] = parentItem;
        return parentIndex;
    }

    private int trySwapDown (int nodeIndex) {
        // if it's at the bottom or can't be moved
        if (nodeIndex == heapSize - 1 || nodeIndex < 0) { return -1; }
        int lIdx = getLeftChildIndex(nodeIndex);
        int rIdx = lIdx + 1;

        // no children
        if (!checkIndex(lIdx)) { return -1; }

        T currNode = heap[nodeIndex];
        T leftChild = heap[lIdx];

        int leftComparison = comparer.compare(currNode, leftChild);
        int highestPriority = leftComparison;
        // if we have a left and a right child
        if (checkIndex(rIdx)) {
            T rightChild = heap[rIdx];
            highestPriority = Math.max(highestPriority, comparer.compare(currNode, rightChild));
        }

        // if smallest < 0 then the current item has less priority
        if (highestPriority <= 0) { return -1; }

        // swap the values
        int modifiedIndex = highestPriority == leftComparison ? lIdx : rIdx;
        heap[nodeIndex] = heap[modifiedIndex];
        heap[modifiedIndex] = currNode;
        return modifiedIndex;
    }

    public T remove () {
        if (heapSize == 0) { return null; }
        swapHeadAndTail();
        heapSize--;
        int insIndex = 0;
        while (insIndex >= 0) { insIndex = trySwapDown(insIndex); }
        return heap[heapSize];
    }

    public void hide () {
        T curr = remove();
        heap[heapSize] = curr;
    }

    public T check () {
        if (heapSize == 0) { return null; }
        return heap[0];
    }

    public boolean insert (T newItem) {
        if (heapSize == heap.length) { return false; }
        int insIndex = heapSize;
        heapSize++;
        heap[insIndex] = newItem;

        while (insIndex > 0) { insIndex = trySwapUp(insIndex); }
        return true;
    }

    public T replace (T newItem) {
        if (heapSize == 0) { return null; }
        int insertAt = 0;
        T curr = heap[insertAt];
        heap[insertAt] = newItem;
        while (insertAt >= 0) { insertAt = trySwapDown(insertAt); }
        return curr;
    }
}
