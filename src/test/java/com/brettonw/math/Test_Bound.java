package com.brettonw.math;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class Test_Bound {
    @Test
    public void testBound () {
        Bound b = new Bound (34, 45, 23, 99, 98, 35);
        assertTrue (Utility.close (b.getMin (), 23));
        assertTrue (Utility.close (b.getMax (), 99));
        assertTrue (Utility.close (b.getSpan (), 99 - 23));
        assertTrue (Utility.close (b.getMid (), (99 + 23) / 2.0));
    }

    @Test
    public void testBoundMap () {
        Bound b = new Bound (34, 45, 23, 99, 98, 35);
        assertTrue (Utility.close (b.mapFromCanonical (0), 23));
        assertTrue (Utility.close (b.mapFromCanonical (1), 99));
        assertTrue (Utility.close (b.mapFromCanonical (0.5), b.getMid ()));
    }

    @Test
    public void testMapCoordinates () {
        Bound[] bounds = new Bound[] { new Bound (0, 10), new Bound (20, 10), new Bound (5, 25) };
        Tuple tuple = new Tuple (0.25, 0.5, 0.75);
        Tuple coordinate = Bound.mapFromCanonical (bounds, tuple);
        assertTrue (Utility.close (Tuple.deltaNorm (new Tuple (2.5, 15, 20), coordinate), 0));
        Tuple canonical = Bound.mapToCanonical (bounds, coordinate);
        assertTrue (Utility.close (Tuple.deltaNorm (tuple, canonical), 0));
    }
}
