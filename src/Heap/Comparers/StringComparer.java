package Heap.Comparers;

import Heap.Comparer;

public class StringComparer implements Comparer<String> {
    public int compare (String a, String b) {
        return a.length() - b.length();
    }
}
