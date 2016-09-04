package com.brettonw.math;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertTrue;

public class Test_DataSet {
    @Test
    public void testDataSet () {
        // generate a whole bunch of random 2D points in 3 clusters (each fairly well separated)
        Bound[][] bounds = new Bound[][] {
                new Bound[] {new Bound (10, 20), new Bound (10, 30)},
                new Bound[] {new Bound (15, 25), new Bound (60, 80)},
                new Bound[] {new Bound (65, 90), new Bound (40, 50)}
        };

        // how many clusters to generate, the dimensionality of the data, and how many points to
        // generate in preparation for the test
        int c = bounds.length;
        int k = bounds[0].length;
        int n = 10000;

        // make the samples, a bit randomly
        Random random = new Random ();
        Tuple[] tuples = new Tuple[n];
        Tuple[] coord = new Tuple[c];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < c; ++j) {
                coord[j] = Bound.mapFromCanonical (bounds[j], Tuple.random (k));
            }
            // randomly pick one of the three
            tuples[i] = coord[random.nextInt (c)];
        }

        // create a  dataset
        DataSet dataSet = new DataSet (tuples);

        // do a range search, and confirm all the found points meet the criteria
        Tuple locus = new Tuple (70.0, 40.0);
        double range = 2.0;
        int[] rangeSearchResult = dataSet.rangeSearch (locus, range);
        assertTrue (rangeSearchResult.length > 0);
        for (int i : rangeSearchResult) {
            assertTrue (Tuple.deltaNorm (tuples[i], locus) < range);
        }

        // now do an exhaustive search over all the tuples to confirm that no other tuples should
        // have matched the criteria
        int exhaustiveMatchCount = 0;
        for (Tuple tuple : tuples) {
            if (Tuple.deltaNorm (tuple, locus) < range) {
                ++exhaustiveMatchCount;
            }
        }
        assertTrue (exhaustiveMatchCount == rangeSearchResult.length);
    }
}
