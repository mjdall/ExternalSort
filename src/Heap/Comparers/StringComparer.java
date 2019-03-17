/**
 * String Comparer implementation
 *
 * Daniel Stokes - 1331134
 * Morgan Dally - ???????
 */

package Heap.Comparers;

import Heap.Comparer;

public class StringComparer implements Comparer<String> {
    public int compare (String a, String b) {
        return a.compareTo(b);
    }
}
