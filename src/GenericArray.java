/**
Copyright (c) 2015, Lars Butler

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */
/**
 * The foundation of the GenericArray class was given to me by Prof. Alin Suciu.
 * The original author and licensing of that code is unclear.
 */

import java.util.Arrays;


public class GenericArray <E>
{
    private E [] array;
    /**
     * Index of the last item in the array.
     * Ranges from [-1, N-1], where array size is N.
     * -1 indicates that the array has no items in it (regardless of capacity).
     */
    private int lastIndex;
    /**
     * 
     * @param capacity: initial capacity of the array
     */
    public GenericArray(int capacity) {
        array = (E[]) new Object[capacity];
        lastIndex = -1;
    }
    public GenericArray() {
        // Reuse the other constructor with the parameter, to not duplicate code.
        this(10);
    }
    public int size() {
        return lastIndex + 1;
    }
    /**
     * Get the underlying storage array. The array is copied , so no direct
     * changes can be made to the array which would corrupt the data structure.
     * Since this is the case, this can potentially be an expensive operation.
     * 
     * This method is mostly for testing.
     * 
     * @return a copy of the array
     */
    public E [] getArray() {
        return Arrays.copyOf(array, array.length);
    }

    public E get(int i) {
        if (i < 0 || i > lastIndex) {
            // Same error message raised by ArrayList if you try to do the same
            // thing (to `get` an item with any index from an empty array.
            throw new IndexOutOfBoundsException(
                    String.format("Index: %d, Size: 0", i));
        }
        return array[i];
    }
    public void set(int i, E value) {
        if (i >= 0 && i <= lastIndex) {
            array[i] = value;
        }
        else {
            throw new IndexOutOfBoundsException(
                    String.format("Index: %d", i));
        }
    }
    public void add(E value) {
        if (isFull()) {
            growArray();
        }
        lastIndex++;
        array[lastIndex] = value;
    }
    public boolean isFull(){
        return size() == array.length;
    }
    public void remove(int i) {
        if (!(i < 0 || i > lastIndex)) {
            for (int j = i; j < lastIndex; j++)
                array[j] = array[j+1];
            lastIndex--;
        }
        else {
            throw new IndexOutOfBoundsException(
                    String.format("Index: %d", i));
        }
    }

    public void insert(int i, E value) {
        // If the array is empty, lastIndex == -1
        // So, we allow the user to insert items at position 0.
        if (i >= 0 && i <= lastIndex + 1) {
            if (isFull()) {
                growArray();
            }
            for (int j = lastIndex; j >= i; j--) {
                array[j+1] = array [j];
            }
            array[i] = value;
            lastIndex++;
        }
        else {
            throw new IndexOutOfBoundsException(
                    String.format("Index: %d", i));
        }

    }

    public void display() {
        for (int i = 0; i <= lastIndex; i++)
            System.out.print(array[i] + " ");
        System.out.println();
    }

    /**
     * Reverse the ordering of items in the storage array, in situ.
     */
    public void reverse() {
        if (lastIndex < 0) {
            // array is empty; do nothing.
            return;
        }
        int midPoint = lastIndex / 2;
        for (int i = 0; i <= midPoint; i++) {
            E buffer;
            int swapIndex = lastIndex - i;
            buffer = array[swapIndex];
            array[swapIndex] = array[i];
            array[i] = buffer;
        }
    }
    /**
     * Double the capacity of the current storage array.
     * Inspired by the implementation of ArrayList from the Java stdlib.
     */
    private void growArray() {
        int initCapacity = array.length;
        int newCapacity = initCapacity * 2;
        array = Arrays.copyOf(array, newCapacity);
    }

}