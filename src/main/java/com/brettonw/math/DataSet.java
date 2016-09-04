package com.brettonw.math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DataSet {
    private static final Logger log = LogManager.getLogger (DataSet.class);
    protected Tuple[] tuples;     // n k-valued tuples
    protected Bound[] bounds;     // k 2-valued bounds

    protected int n;              // number of tuples
    protected int k;              // dimensionality of the cluster space

    public DataSet (Tuple... tuples) {
        setTuples (tuples);
    }

    public void setTuples (Tuple... tuples) {
        this.tuples = tuples;
        n = tuples.length;
        if (n > 0) {
            // create the bounds objects
            bounds = Bound.getBounds (tuples);
            k = bounds.length;

            // add a little bit of buffer so that the contents are entirely enclosed
            Bound.resize (bounds, 1.0 + 1.0e-6);
        }

        // spew the stats
        log.info ("N: " + n);
        log.info ("K: " + k);
        for (int i = 0; i < k; ++i) {
            log.info ("Bounds " + i + "(" + bounds[i].getMin () + ", " + bounds[i].getMax () + ")");
        }
    }

    public int[] rangeSearch (Tuple locus, double range) {
        // naive scan of the full list
        List<Integer> list = new ArrayList<> ();

        // naive scan, an exhaustive search over all the tuples to find tuples in the range
        double rangeSq = range * range;
        for (int i = 0; i < n; ++i) {
            if (Tuple.deltaNormSq (tuples[i], locus) < rangeSq) {
                list.add (i);
            }
        }

        return Utility.IntegerListToIntArray (list);
    }

    public Tuple[] getTuples (int[] selection) {
        Tuple[] result = new Tuple[selection.length];
        for (int i = 0, selectionLength = selection.length; i < selectionLength; ++i) {
            result[i] = tuples[selection[i]];
        }
        return result;
    }

    public int getN () { return n; }
    public int getK () { return k; }
    public Bound[] getBounds () { return bounds; }
    public Tuple get (int i) { return tuples[i]; }
}
