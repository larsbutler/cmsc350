
/**
 * Most of the credit for this code goes to:
 * http://opendatastructures.org/ods-java/2_Array_Based_Lists.html
 * Code from opendatastructures.org is licensed under the Creative Commons
 * License. See:
 *   - http://opendatastructures.org/
 *   - https://creativecommons.org/licenses/by/2.5/ca/
 *
 * I wanted to include a proper copyright notice, but opendatastructures.org
 * does not post any such copyright notice (at least none that I could find).
 *
 * My major contribution to this is researching the generic stuff and
 * implementing `newArray`. In the guide at opendatastructures.org, `newArray`
 * is referenced all over the place, but I never saw any implementation
 * details--contrary to main public methods of this class, for which the guide
 * included complete, working code.
 *
 * Dynamically instantiating generic arrays turns out to be really tricky.
 * Who knew? :)
 * I got a lot of help from this thread, in order to implement `newArray`:
 * http://stackoverflow.com/questions/3403909/get-generic-type-of-class-at-runtime
 */

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class RootishArrayStack <E> {

    protected List<E[]> blocks;
    private int size = 0;
    private final Class<E> type;

    /**
     * @param type - It turns out, dynamically creating generic arrays is very
     * tricky, so we need to explicitly pass in the runtime type, in addition
     * to a generic type parameter. For example, we must do this:
     * RootishArrayStack<String> ras = new RootishArrayStack<>(String.class);
     */
    public RootishArrayStack(Class <E> type) {
        this(10, type);
    }

    /**
     * @param initCapacity - Initial number of items which can potentially
     * be stored
     * @param type - It turns out, dynamically creating generic arrays is very
     * tricky, so we need to explicitly pass in the runtime type, in addition
     * to a generic type parameter. For example, we must do this:
     * RootishArrayStack<String> ras = new RootishArrayStack<>(String.class);
     */
    public RootishArrayStack(int initCapacity, Class<E> type) {
        this.type = type;
        if (initCapacity <= 0) {
            throw new RuntimeException("Initial capacity must be > 0");
        }
        // TODO: check for 0 or negative values

        // Calculate the number of total blocks, using our `i2b` helper function
        int highestIndex = initCapacity - 1;  // quantity to index: -1
        int numBlocks = i2b(highestIndex) + 1;  // index to quantity: +1

        blocks = new ArrayList<E[]>(numBlocks);
        // Initialize each block. The first block will have a size of 1,
        // the second block will have a size of 2, the third a size of 3,
        // and so on.
        for (int i = 0; i < numBlocks; i++) {  // quantity to index: -1
            @SuppressWarnings("unchecked")
            E[] arr = newArray(i + 1);
            blocks.add(arr);
        }
    }

    public int size() {
        return size;
    }

    protected static int i2b(int i) {
        double db = (-3.0 + Math.sqrt(9 + 8 * i)) / 2.0;
        int b = (int)Math.ceil(db);
        return b;
    }

    /**
     * Add a new block. The size of the new block should be equal to the size
     * of the previous block + 1.
     */
    private void grow() {
        blocks.add(newArray(blocks.size() + 1));
    }

    /**
     * Chop away unused blocks.
     */
    private void shrink() {
        int r = blocks.size();
        while (r > 0 && (r - 2) * (r - 1) / 2 >= size) {
            blocks.remove(blocks.size() - 1);
            r--;
        }
    }

    /**
     * Utility method for creating new arrays for the storage structure.
     */
    @SuppressWarnings("unchecked")
    private E[] newArray(int length) {
        return (E[]) Array.newInstance(type, length);
    }

    public void add(int i, E x) {
        if (i < 0 || i > size) {
            throw new IndexOutOfBoundsException();
        }
        int r = blocks.size();
        if (r * (r + 1)/2 < size + 1) {
            grow();
        }
        size++;
        // Shift values and make room for the new element:
        for (int j = size - 1; j > i; j--) {
            set(j, get(j-1));
        }
        set(i, x);
    }

    public E remove(int i) {
        if (i < 0 || i > size - 1) {
            throw new IndexOutOfBoundsException();
        }
        E x = get(i);
        for (int j = i; j < size - 1; j++) {
            set(j, get(j + 1));
        }
        size--;
        int r = blocks.size();
        if ((r - 2) * (r - 1) / 2 >= size) {
            shrink();
        }
        return x;
    }

    public E set(int i, E x) {
        if (i < 0 || i > size - 1) {
            throw new IndexOutOfBoundsException();
        }
        int b = i2b(i);
        int j = i - b * (b + 1) / 2;
        // We could technically just do this:
        // T y = get(i);
        // But this is faster (no redundant calculation of `b` and `j`):
        E y = blocks.get(b)[j];
        blocks.get(b)[j] = x;
        return y;
    }

    public E get(int i) {
        if (i < 0 || i > size - 1) {
            throw new IndexOutOfBoundsException();
        }
        int b = i2b(i);
        int j = i - b * (b + 1) / 2;
        return blocks.get(b)[j];
    }

    /**
     * Flatten the structure to a single array.
     */
    public E[] toArray() {
        E[] arr = newArray(size);
        for (int i = 0; i < size; i++) {
            arr[i] = get(i);
        }
        return arr;
    }
}
