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

import java.util.Arrays;

public class GenericArrayTest extends Testable {

    public static final boolean VERBOSE = true;
    private GenericArray<String> arr;

    @Override
    public void setUp() {
        arr = new GenericArray<>(3);
    }

    @Test
    public void testGet_empty() {
        try {
            arr.get(0);
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
            return;
        }
    }

    @Test
    public void testGet_typical() {
        arr.add("foo");
        assertEqual("foo", arr.get(0));
        arr.add("bar");
        assertEqual("bar", arr.get(1));
    }


    @Test
    public void testSet_empty() {
        arr = new GenericArray<>(0);
        try{
            arr.set(0, "foo");
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
            return;
        }
    }

    @Test
    public void testSet_typical() {
        arr = new GenericArray<>(2);
        arr.add("replaceme");
        assertEqual(arr.get(0), "replaceme");
        arr.set(0, "foo");
        assertEqual(arr.get(0), "foo");

        for (int i: new int [] {-1, 1, 2}) {
            try {
                arr.set(i, "foo");
                fail();
            }
            catch (IndexOutOfBoundsException e) {
                // expected
            }
        }
    }

    /**
     * Common boilerplate code for the testAdd_* methods.
     */
    private void testAdd_setup() {
        assertEqual(arr.size(), 0);
        arr.add("foo");
        assertEqual(arr.size(), 1);
        arr.add("bar");
        assertEqual(arr.size(), 2);
        arr.add("baz");
        assertEqual(arr.size(), 3);
        assertThat(Arrays.equals(
                arr.getArray(),
                new String [] {"foo", "bar", "baz"}));
    }

    @Test
    public void testAdd_typical() {
        testAdd_setup();

    }

    @Test
    public void testAdd_full() {
        // Test that the array grows itself.
        testAdd_setup();
        // Add a fourth item
        arr.add("blarg");
        assertEqual(arr.size(), 4);
        // The array should have doubled from the original size.
        assertThat(Arrays.equals(
                arr.getArray(),
                new String [] {"foo", "bar", "baz", "blarg", null, null}));
    }

    @Test
    public void testIsFull() {
        arr = new GenericArray<>(2);
        assertThat(!arr.isFull());
        arr.add("foo");
        assertThat(!arr.isFull());
        arr.add("bar");
        // It should be full now
        assertThat(arr.isFull());
    }

    @Test
    public void testRemove_failures() {
        for (int i: new int [] {-1, 0}) {
            try {
                arr.remove(i);
                fail();
            }
            catch (IndexOutOfBoundsException e) {
                // expected
            }
        }

        arr.add("foo");
        try {
            arr.remove(1);
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testRemove_success() {
        arr.add("foo");
        arr.add("bar");
        assertThat(Arrays.equals(
                arr.getArray(),
                new String [] {"foo", "bar", null}));
        arr.remove(0);
        assertThat(Arrays.equals(
                arr.getArray(),
                // "bar" appears twice, but the second one should be
                // inaccessible
                new String [] {"bar", "bar", null}));
        assertEqual(arr.size(), 1);
        try {
            // Try to get the second "bar" by index:
            arr.get(1);
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }
        // Now lets add another item into the array,
        // and make sure it overwrites the second "bar".
        arr.add("foo");
        assertThat(Arrays.equals(
                arr.getArray(),
                new String [] {"bar", "foo", null}));
    }

    @Test
    public void testInsert_empty_array() {
        try {
            arr.insert(1, "foo");
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }
        // We can, however, insert at index=0
        arr.insert(0, "foo");
    }

    @Test
    public void testInsert_nonempty_array() {
        arr.add("third");
        arr.insert(0, "first");
        arr.insert(1, "second");
        assertThat(Arrays.equals(
                arr.getArray(),
                new String [] {"first", "second", "third"}));

        arr.insert(3, "fourth");
        assertThat(Arrays.equals(
                arr.getArray(),
                // We expect two `null` values at the end, due to growth.
                new String [] {"first", "second", "third", "fourth", null, null}));
    }

    @Test
    public void testReverse() {
        // Test with empty array.
        assertThat(Arrays.equals(
                arr.getArray(),
                new String [] {null, null, null}));
        arr.reverse();
        assertThat(Arrays.equals(
                arr.getArray(),
                new String [] {null, null, null}));

        // Test with one item.
        arr.add("foo");
        arr.reverse();
        assertThat(Arrays.equals(
                arr.getArray(),
                new String [] {"foo", null, null}));

        // Test with two items.
        arr.add("bar");
        assertThat(Arrays.equals(
                arr.getArray(),
                new String [] {"foo", "bar", null}));
        arr.reverse();
        assertThat(Arrays.equals(
                arr.getArray(),
                new String [] {"bar", "foo", null}));

        // Test with three items.
        arr.add("baz");
        assertThat(Arrays.equals(
                arr.getArray(),
                new String [] {"bar", "foo", "baz"}));
        arr.reverse();
        assertThat(Arrays.equals(
                arr.getArray(),
                new String [] {"baz", "foo", "bar"}));
    }

    public static void main (String [] args) {
        new GenericArrayTest().runTests(VERBOSE);
    }
}
