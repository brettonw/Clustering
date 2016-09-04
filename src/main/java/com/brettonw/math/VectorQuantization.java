package com.brettonw.math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// https://en.wikipedia.org/wiki/Vector_quantization
// https://www.youtube.com/watch?v=mfqmoUN-Cuw
public class VectorQuantization extends ClusterAlgorithm {
    private static final Logger log = LogManager.getLogger (VectorQuantization.class);

    private int clusterCount;
    private Tuple[] clusterCentroids;
    private int[] assign;

    public VectorQuantization (DataSet dataSet, int clusterCount) {
        super (dataSet);
        this.clusterCount = clusterCount;

        // create the 'assign' array
        assign = new int[dataSet.getN ()];

        // pick 'clusterCount' random points from the dataSet to start the algorithm
        Random random = new Random (System.currentTimeMillis ());
        clusterCentroids = new Tuple[clusterCount];
        for (int i = 0; i < clusterCount; ++i) {
            clusterCentroids[i] = dataSet.get (random.nextInt (dataSet.getN ()));
        }

        log.info ("--------------------");
        log.info ("Start");

        double delta = 0;
        do {
            log.info ("Delta: " + delta);
            for (int i = 0; i < clusterCount; ++i) {
                log.info ("Centroid " + clusterCentroids[i].toString ());
            }
            Tuple[] newCentroids = step (clusterCentroids);

            // compute the delta from this step
            delta = 0;
            for (int i = 0; i < clusterCount; ++i) {
                delta += Tuple.deltaNormSq (clusterCentroids[i], newCentroids[i]);
            }
            clusterCentroids = newCentroids;
        } while (delta > 0);

        log.info ("--------------------");
        log.info ("Finished");
        for (int i = 0; i < clusterCount; ++i) {
            log.info ("Centroid " + clusterCentroids[i].toString ());
        }
    }

    private Tuple[] step (Tuple... centroids) {
        int c = centroids.length;

        // this procedure is guaranteed to converge

        log.info ("--------------------");
        log.info ("Step");

        // loop over all of the tuples
        for (int i = 0, n = dataSet.getN (); i < n; ++i) {
            Tuple tuple = dataSet.get (i);
            // determine which centroid the tuple is closest to
            int nearestIndex = 0;
            double nearestNormSq = Tuple.deltaNormSq (centroids[0], tuple);
            for (int j = 1; j < c; ++j) {
                double normSq = Tuple.deltaNormSq (centroids[j], tuple);
                if (normSq < nearestNormSq) {
                    nearestNormSq = normSq;
                    nearestIndex = j;
                }
            }

            // save the tuple into that cluster
            assign[i] = nearestIndex;
        }

        // now gather each cluster to compute new centroids
        Tuple[] newCentroids = new Tuple[c];
        for (int i = 0; i < c; ++i) {
            Tuple[] clusterTuples = getCluster (i);
            newCentroids[i] = Tuple.average (clusterTuples);
            if (newCentroids[i] == null) {
                newCentroids[i] = centroids[i];
            }
        }
        return newCentroids;
    }

    @Override
    public int getClusterCount () {
        return clusterCount;
    }

    @Override
    public Tuple[] getCluster (int i) {
        // naive scan of the full list
        List<Tuple> list = new ArrayList<> ();

        // an exhaustive search over all the tuples to find tuples in the cluster
        for (int j = 0, n = dataSet.getN (); j < n; ++j) {
            if (assign[j] == i) {
                list.add (dataSet.get (j));
            }
        }

        return list.toArray (new Tuple[list.size ()]);
    }
}
