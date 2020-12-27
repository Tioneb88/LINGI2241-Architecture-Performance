package utils;

import java.util.concurrent.LinkedBlockingDeque;

/*
 * Small class to manage the buffer.
 */
public class Buffer<T> extends LinkedBlockingDeque<T> {

    /*
     * Constructs an object Buffer with a maximal size.
     * @param size : the maximal number of elements in the buffer at the same time
     * @return None
     */
    public Buffer(int size) {
        super(size);
    }

    /*
     * Adds a new element in the buffer.
     * @param newElement : the element to add
     * @return : true if added (buffer has still free space) otherwise false
     */
    public synchronized boolean add(T newElement) {
        if (this.remainingCapacity() > 0) {
            super.add(newElement);
            return true;
        }
        return false;
    }
}