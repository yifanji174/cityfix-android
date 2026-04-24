package com.g04.cityfix.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;

/**
 * @author Junhao Liu
 */
public class PeekableTest {
    private final int[] arr = new int[]{1, 2, 3};
    @Test
    public void nextTest() {
        Peekable<Integer> it = new Peekable<>(Arrays.stream(arr).iterator());
        assertEquals(1, (long)it.next());
        assertEquals(2, (long)it.next());
        assertEquals(3, (long)it.next());
    }
    @Test
    public void peekTest() {
        Peekable<Integer> it = new Peekable<>(Arrays.stream(arr).iterator());
        assertEquals(1, (long)it.peek());
        assertEquals(1, (long)it.peek());
        assertEquals(1, (long)it.next());
        assertEquals(2, (long)it.peek());
        it.next();
        assertTrue(it.hasNext());
        assertEquals(3, (long)it.peek());
        assertTrue(it.hasNext());
        assertEquals(3, (long)it.next());
        assertFalse(it.hasNext());
    }
}
