package com.brettonw.math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SpatiallyIndexed extends DataSet implements Comparator<Tuple> {
    private static final Logger log = LogManager.getLogger (SpatiallyIndexed.class);

    protected int q;              // quantization of the tuples in grid space
    protected int[] index;        // grid-based index into the tuple list

    public SpatiallyIndexed (Tuple... tuples) {
        super (tuples);
    }

    @Override
    public void setTuples (Tuple... tuples) {
        super.setTuples (tuples);

        // compute the quantization of the tuples for ordering purposes, such that the quantization
        // is equivalent to the expected grid cell occupancy - this is a heuristic I am using to
        // avoid thrashing the cache
        q = (int) Math.ceil (Math.pow (n, 1.0 / (k + 1)));

        // sort the array using this quantization. this produces something akin to a Morton ordering
        // of the array. I'm not using swizzling, but this ordering gives some nice locality
        // properties to the access characteristics of the algorithm during execution
        Arrays.sort (tuples, this);

        // build a k-D index (in a 1-D array)
        int indexSize = q;
        for (int i = 1; i < k; ++i) {
            indexSize *= q;
        }
        index = new int[indexSize];

        // now loop over the tuples to fill the index
        int occupiedCellCount = 0;
        int lastIndexOffset = -1;
        for (int i = 0; i < n; ++i) {
            Tuple tuple = tuples[i];
            int[] grid = mapToGrid (tuple);
            int indexOffset = indexOffsetFromGrid (grid);
            if (lastIndexOffset != indexOffset) {
                log.debug ("i (" + i + "), coordinate " + tuple.toString () + ", grid " + gridToString (grid) + ", indexOffset (" + indexOffset + ")");
                ++occupiedCellCount;

                // back fill any cells that might have been empty
                while (lastIndexOffset < indexOffset) {
                    index[++lastIndexOffset] = i;
                }
            }
        }

        // back fill from the end of the last cell
        int end = indexSize - 1;
        while (lastIndexOffset < end) {
            index[++lastIndexOffset] = n;
        }

        // spew the stats
        log.info ("Q: " + q);
        log.info ("occupied cells: " + occupiedCellCount);
        log.info ("occupancy: " + (n / occupiedCellCount));
    }

    public int[] mapToGrid (Tuple coordinate) {
        double[] values = Tuple.scale (Bound.mapToCanonical (bounds, coordinate), q).getValues ();
        int[] result = new int[k];
        for (int i = 0; i < k; ++i) {
            result[i] = (int) Math.floor (values[i]);
        }
        return result;
    }

    public Tuple mapFromGrid (int[] grid) {
        double[] values = new double[k];
        for (int i = 0; i < k; ++i) {
            values[i] = (double) grid[i];
        }
        Tuple canonical = Tuple.scale (new Tuple (values), 1.0 / q);
        return Bound.mapFromCanonical (bounds, canonical);
    }

    @Override
    public int compare (Tuple a, Tuple b) {
        int[] aGrid = mapToGrid (a);
        int[] bGrid = mapToGrid (b);
        for (int i = 0; i < k; ++i) {
            int delta = aGrid[i] - bGrid[i];
            if (delta != 0) {
                return delta;
            }
        }
        return 0;
    }

    public int indexOffsetFromGrid (int[] grid) {
        int indexOffset = 0;
        for (int i = 0; i < k; ++i) {
            if ((grid[i] >= 0) && (grid[i] < q)) {
                indexOffset = (indexOffset * q) + grid[i];
            } else {
                return -1;
            }
        }
        return indexOffset;
    }

    public String gridToString (int[] grid) {
        StringBuilder stringBuilder = new StringBuilder ().append ("(");
        stringBuilder.append (grid[0]);
        for (int i = 1; i < k; ++i) {
            stringBuilder.append (",").append (grid[i]);
        }
        return stringBuilder.append (")").toString ();
    }

    private void rangeSearchWorker (Tuple locus, double rangeSq, List<Integer> list, int[] grid, int[] minGrid, int[] maxGrid, int current) {
        if (current < k) {
            // recur
            for (int i = minGrid[current]; i <= maxGrid[current]; ++i) {
                grid[current] = i;
                rangeSearchWorker (locus, rangeSq, list, grid, minGrid, maxGrid, current + 1);
            }
        } else {
            // the grid component is complete, look it up and loop over the tuples referenced there
            int indexOffset = indexOffsetFromGrid (grid);
            if (indexOffset >= 0) {
                int cellStart = index[indexOffset];
                int nextIndexOffset = indexOffset + 1;
                int cellEnd =  (nextIndexOffset < index.length) ? index[nextIndexOffset] : n;
                for (int i = index[indexOffset]; i < cellEnd; ++i) {
                    // check to see if the candidate is within the specified range of the search locus
                    Tuple candidate = tuples[i];
                    if (Tuple.deltaNormSq (locus, candidate) < rangeSq) {
                        list.add (i);
                    }
                }
            }
        }
    }

    @Override
    public int[] rangeSearch (Tuple locus, double range) {
        // create a list to store the found tuples
        List<Integer> list = new ArrayList<> ();

        // construct a min and max tuple for the range we want to search
        Tuple offset = new Tuple (k, range);
        Tuple minTuple = Tuple.add (locus, Tuple.scale (offset, -1.0));
        Tuple maxTuple = Tuple.add (locus, offset);

        // map that to the grid, and invoke a recursive worker to iterate over the grid cells
        int[] minGrid = mapToGrid (minTuple);
        int[] maxGrid = mapToGrid (maxTuple);
        rangeSearchWorker (locus, range * range, list, new int[k], minGrid, maxGrid, 0);

        return Utility.IntegerListToIntArray (list);
    }
}
