package com.brettonw.math;

import com.brettonw.bedrock.bag.BagArray;

public abstract class ClusterAlgorithm {
    protected DataSet dataSet;

    protected ClusterAlgorithm (DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public DataSet getDataSet () {
        return dataSet;
    }

    public abstract int getClusterCount ();
    public abstract Tuple[] getCluster (int i);

    public BagArray export () {
        int clusterCount = getClusterCount ();
        BagArray bagArray = new BagArray (clusterCount);
        for (int i = 0; i < clusterCount; ++i) {
            Tuple[] tuples = getCluster (i);
            int clusterSize = tuples.length;
            BagArray clusterBagArray = new BagArray (clusterSize);
            for (Tuple tuple : tuples) {
                double[] values = tuple.getValues ();
                int valuesSize = values.length;
                BagArray valuesBagArray = new BagArray (valuesSize);
                for (double value : values) {
                    valuesBagArray.add (value);
                }
                clusterBagArray.add (valuesBagArray);
            }
            bagArray.add (clusterBagArray);
        }
        return bagArray;
    }
}
