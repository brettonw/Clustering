package com.brettonw.math;

import java.util.List;

public class Utility {
    public static boolean close (double a, double b) {
        return (Math.abs (a - b) < 1.0e-6);
    }

    public static int[] IntegerListToIntArray (List<Integer> list) {
        // convert the result list to an array, no library already does this...
        int resultSize = list.size ();
        int[] result = new int[resultSize];
        for (int i = 0; i < resultSize; ++i) {
            result[i] = list.get (i);
        }
        return result;
    }
}
