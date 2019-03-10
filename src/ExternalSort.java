import Heap.Heap;
import Heap.Comparers.StringComparer;

public class ExternalSort {

    public static void popAll (Heap<String> priorityQueue, boolean verbose) {
        String removed = priorityQueue.remove();
        while (removed != null) {
            if (verbose) { System.out.println(removed); }
            removed = priorityQueue.remove();
        }
    }

    public static void printHeap (String[] heap) {
        for (int i = 0; i < heap.length; i++) {
            System.out.println(heap[i]);
        }
    }

    public static void main (String[] args) {
        String[] sortMe = new String[]{"Hi", "a", "b", "c", "dddd", "Dwqfee", "Howw", "hOW", "idw", "GFEgregre", "fewfewfe", "fwefewfwefwefe"};
        StringComparer comparer = new StringComparer();
        Heap<String> priorityQueue = new Heap<String>(sortMe, comparer);
        priorityQueue.insert("a");
        popAll(priorityQueue, false);
        priorityQueue.insert("bbbbbb");
        priorityQueue.insert("a");
        String found = priorityQueue.replace("regrger");
        System.out.println("Found: " + found);
        popAll(priorityQueue, true);
    }
}
