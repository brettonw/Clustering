package com.brettonw.math;

import com.brettonw.bedrock.bag.BagArray;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class Test_DensityBasedScan {
    @Test
    public void testDBScan () {
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
        int n = 1000;

        // make the samples, a bit randomly
        Random random = new Random ();
        Tuple[] tuples = new Tuple[n];
        Tuple[] coord = new Tuple[c];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < c; ++j) {
                coord[j] = Bound.mapFromCanonical (bounds[j], Tuple.random (k));
            }
            // randomly pick one of the bounds
            tuples[i] = coord[random.nextInt (c)];
        }

        // create a spatially indexed dataset
        DataSet dataSet = new SpatiallyIndexed (tuples);

        // now create the DensityBasedScan solver, and test that it correctly clusters...
        double range = 2.0;
        DensityBasedScan densityBasedScan = new DensityBasedScan (dataSet, range, 2);
        assertTrue (densityBasedScan.getClusterCount () == 3);

        BagArray clustersBagArray = densityBasedScan.export ();
        File outputFile = new File ("target", "dbs.json");
        try (BufferedWriter writer = new BufferedWriter (new FileWriter (outputFile))) {
            writer.write (clustersBagArray.toString ());
        } catch (IOException exception) { }
    }
}
