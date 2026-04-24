package com.g04.cityfix.common.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Sonia Lin
 * @param <T>
 */
public class Peekable<T> implements Iterator<T> {
    private Iterator<T> iter;
    private T cache;
    private boolean hasNxt;

    public Peekable(Iterator<T> iter) {
        this.iter = iter;
        this.hasNxt = iter.hasNext();
        if (this.hasNxt) {
            this.cache = iter.next();
        }
    }

    @Override
    public T next() {
        T ret = cache;
        this.hasNxt = iter.hasNext();
        if (this.hasNxt) {
            this.cache = iter.next();
        }
        return ret;
    }

    @Override
    public boolean hasNext() {
        return hasNxt;
    }

    public T peek() {
        if (!this.hasNxt) {
            throw new NoSuchElementException();
        }
        return cache;
    }
}
