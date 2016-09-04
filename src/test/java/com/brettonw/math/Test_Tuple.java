package com.brettonw.math;

import com.brettonw.bag.BagObject;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class Test_Tuple {
    @Test
    public void testTupleConstructor () {
        Tuple a = new Tuple (1.0, 2.0, 3.0);
        assertTrue (Utility.close (a.getValues ()[0], 1.0));
        assertTrue (Utility.close (a.getValues ()[1], 2.0));
        assertTrue (Utility.close (a.getValues ()[2], 3.0));
    }

    @Test
    public void testTupleCopyConstructor () {
        Tuple a = new Tuple (1.0, 2.0, 3.0);
        Tuple b = new Tuple (a);
        assertTrue (Utility.close (b.getValues ()[0], 1.0));
        assertTrue (Utility.close (b.getValues ()[1], 2.0));
        assertTrue (Utility.close (b.getValues ()[2], 3.0));
    }

    @Test
    public void testTupleBagConstructor () {
        BagObject bagObject = new BagObject ()
                .put ("u", 1.5)
                .put ("v", 2.5)
                .put ("x", 3.5)
                .put ("y", 4.5)
                .put ("z", 5.5)
                .put ("w", 6.5);
        Tuple abc = new Tuple (bagObject, "x", "y", "z");
        assertTrue (Utility.close (abc.getValues ()[0], 3.5));
        assertTrue (Utility.close (abc.getValues ()[1], 4.5));
        assertTrue (Utility.close (abc.getValues ()[2], 5.5));
    }

    @Test
    public void testTupleDeltas () {
        Tuple a = new Tuple (1.0, 2.0, 3.0);
        Tuple b = new Tuple (3.5, 4.5, 5.5);
        Tuple delta = Tuple.delta (a, b);
        assertTrue (Utility.close (Tuple.deltaNorm (a, b), Math.sqrt ((2.5 * 2.5) * 3)));
    }

    @Test
    public void testTupleNormalize () {
        Tuple a = new Tuple (1.0, 2.0, 3.0);
        assertTrue (Utility.close (Tuple.norm (Tuple.normalize (a)), 1.0));
    }

    @Test
    public void testTupleAdd () {
        Tuple a = new Tuple (1.0, 2.0, 3.0);
        Tuple b = new Tuple (3.5, 4.5, 5.5);
        Tuple c = new Tuple (4.5, 5.5, 6.5);
        Tuple x = Tuple.average (a, b, c);
        assertTrue (Utility.close (x.getValues ()[0], (1.0 + 3.5 + 4.5) / 3));
        assertTrue (Utility.close (x.getValues ()[1], (2.0 + 4.5 + 5.5) / 3));
        assertTrue (Utility.close (x.getValues ()[2], (3.0 + 5.5 + 6.5) / 3));
    }
}
