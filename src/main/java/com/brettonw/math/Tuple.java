package com.brettonw.math;

import com.brettonw.bedrock.bag.BagObject;

import java.util.Arrays;
import java.util.Random;

public class Tuple {
    private static Random random = new Random (System.currentTimeMillis ());

    private double[] values;

    public Tuple (double... values) {
        this.values = values;
    }

    public Tuple (int k, double value) {
        values = new double[k];
        Arrays.fill (values, value);
    }

    public Tuple (Tuple a) {
        values = Arrays.copyOf (a.values, a.values.length);
    }

    public Tuple (BagObject bagObject, String... sources) {
        int k = sources.length;
        values = new double[k];
        for (int i = 0; i < k; ++i) {
            values[i] = bagObject.getDouble (sources[i]);
        }
    }

    public double[] getValues () {
        return values;
    }

    public static double normSq (Tuple a) {
        return dot (a, a);
    }

    public static double norm (Tuple a) {
        return Math.sqrt (normSq (a));
    }

    public static double dot (Tuple a, Tuple b) {
        int k = a.values.length;
        double acc = 0;
        for (int i = 0; i < k; ++i) {
            acc += a.values[i] * b.values[i];
        }
        return acc;
    }

    public static Tuple add (Tuple... tuples) {
        // must all be the same size as the first one
        if ((tuples != null) && (tuples.length > 0)) {
            int k = tuples[0].values.length;
            double[] sum = new double[k];
            for (Tuple tuple : tuples) {
                for (int i = 0; i < k; ++i) {
                    sum[i] += tuple.values[i];
                }
            }
            return new Tuple (sum);
        }
        return null;
    }

    public static Tuple average (Tuple... tuples) {
        if ((tuples != null) && (tuples.length > 0)) {
            return scale (add (tuples), 1.0 / tuples.length);
        }
        return null;
    }

    public static Tuple scale (Tuple a, double s) {
        int k = a.values.length;
        double[] values = new double[k];
        for (int i = 0; i < k; ++i) {
            values[i] = a.values[i] * s;
        }
        return new Tuple (values);
    }

    public static Tuple delta (Tuple a, Tuple b) {
        int k = a.values.length;
        double[] values = new double[k];
        for (int i = 0; i < k; ++i) {
            values[i] = a.values[i] - b.values[i];
        }
        return new Tuple (values);
    }

    public static double deltaNormSq (Tuple a, Tuple b) {
        return normSq (delta (a, b));
    }

    public static double deltaNorm (Tuple a, Tuple b) {
        return Math.sqrt (deltaNormSq (a, b));
    }

    public static Tuple normalize (Tuple a) {
        return scale (a, 1.0 / norm (a));
    }

    public static Tuple random (int k) {
        double[] values = new double[k];
        for (int i = 0; i < k; ++i) {
            values[i] = random.nextDouble ();
        }
        return new Tuple (values);
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder ().append ("(").append (values[0]);
        int k = values.length;
        for (int i = 1; i < k; ++i) {
            sb.append (", ").append (values[i]);
        }
        return sb.append (")").toString ();
    }
}
