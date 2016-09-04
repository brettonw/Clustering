package com.brettonw.math;

public class Bound {
    private double min;
    private double max;

    public Bound () {
        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE;
    }

    public Bound (double... values) {
        this ();
        for (double value : values) {
            accumulate (value);
        }
    }

    public double getMin () {
        return min;
    }

    public double getMax () {
        return max;
    }

    public double getSpan () {
        return max - min;
    }

    public double getMid () {
        return (min + max) / 2;
    }

    public double mapFromCanonical (double canonical) {
        return min + ((max - min) * canonical);
    }

    public double mapToCanonical (double coordinate) {
        return (coordinate - min) / (max - min);
    }

    public Bound accumulate (double value) {
        if (value < min) min = value;
        if (value > max) max = value;
        return this;
    }

    public Bound resize (double ratio) {
        double mid = getMid ();
        min = mid - ((mid - min) * ratio);
        max = mid + ((max - mid) * ratio);
        return this;
    }

    public boolean contains (double value) {
        return ((value >= min) && (value <= max));
    }

    public static void accumulate (Bound[] bounds, double... values) {
        int k = bounds.length;
        for (int i = 0; i < k; ++i) {
            bounds[i].accumulate (values[i]);
        }
    }

    public static void accumulate (Bound[] bounds, Tuple tuple) {
        accumulate (bounds, tuple.getValues ());
    }

    public static void accumulate (Bound[] bounds, Tuple[] tuples) {
        for (Tuple tuple : tuples) {
            accumulate (bounds, tuple.getValues ());
        }
    }

    public static Bound[] getBounds (Tuple[] tuples) {
        Bound[] bounds = null;
        if (tuples.length > 0) {
            int k = tuples[0].getValues ().length;
            bounds = new Bound[k];
            for (int i = 0; i < k; ++i) {
                bounds[i] = new Bound ();
            }
            accumulate (bounds, tuples);
        }
        return bounds;
    }

    public static void resize (Bound[] bounds, double ratio) {
        int k = bounds.length;
        for (int i = 0; i < k; ++i) {
            bounds[i].resize (ratio);
        }
    }

    public static boolean contains (Bound[] bounds, double... values) {
        int k = bounds.length;
        for (int i = 0; i < k; ++i) {
            if (! bounds[i].contains (values[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean contains (Bound[] bounds, Tuple tuple) {
        return contains (bounds, tuple.getValues ());
    }

    public static Tuple mapFromCanonical (Bound[] bounds, double... canonical) {
        int k = bounds.length;
        double[] values = new double[k];
        for (int i = 0; i < k; ++i) {
            values[i] = bounds[i].mapFromCanonical (canonical[i]);
        }
        return new Tuple (values);
    }

    public static Tuple mapFromCanonical (Bound[] bounds, Tuple canonical) {
        return mapFromCanonical (bounds, canonical.getValues ());
    }

    public static Tuple mapToCanonical (Bound[] bounds, double... coordinates) {
        int k = bounds.length;
        double[] values = new double[k];
        for (int i = 0; i < k; ++i) {
            values[i] = bounds[i].mapToCanonical (coordinates[i]);
        }
        return new Tuple (values);
    }

    public static Tuple mapToCanonical (Bound[] bounds, Tuple coordinates) {
        return mapToCanonical (bounds, coordinates.getValues ());
    }
}
