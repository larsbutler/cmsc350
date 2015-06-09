import java.util.Arrays;


public class RootishArrayStackTest extends Testable {

    private RootishArrayStack<String> ras;
    @Override
    public void setUp() {
        ras = new RootishArrayStack<>(String.class);
    }
    @Test
    public void testConstructor() {
        // Test default capacity: 10
        assertEqual(ras.blocks.size(), 4);
        assertEqual(ras.blocks.get(0).length, 1);
        assertEqual(ras.blocks.get(1).length, 2);
        assertEqual(ras.blocks.get(2).length, 3);
        assertEqual(ras.blocks.get(3).length, 4);

        // Custom capacity: 3
        ras = new RootishArrayStack<>(3, String.class);
        assertEqual(ras.blocks.size(), 2);
        assertEqual(ras.blocks.get(0).length, 1);
        assertEqual(ras.blocks.get(1).length, 2);

        // Invalid capacity: 0
        try {
            ras = new RootishArrayStack<>(0, String.class);
            fail();
        }
        catch (RuntimeException e) {
            assertEqual("Initial capacity must be > 0", e.getMessage());
        }

        // Invalid capacity: -1
        try {
            ras = new RootishArrayStack<>(-1, String.class);
            fail();
        }
        catch (RuntimeException e) {
            assertEqual("Initial capacity must be > 0", e.getMessage());
        }
    }

    @Test
    public void testI2b() {
        int [] testCases = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        int [] bValues = {0, 1, 1, 2, 2, 2, 3, 3, 3, 3, 4, 4};
        for (int i = 0; i < testCases.length; i++) {
            int testCase = testCases[i];
            int bValue = bValues[i];
            int b = RootishArrayStack.i2b(testCase);
            assertEqual(b, bValue);
        }
    }

    @Test
    public void testAdd_outOfBounds() {
        // negative index: -1
        try {
            ras.add(-1, "foo");
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }

        // index out of bounds: 1
        // (since size is still 0, regardless of capacity
        try {
            ras.add(1, "foo");
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testAdd_append() {
        ras = new RootishArrayStack<>(4, String.class);
        ras.add(0, "foo");
        ras.add(1, "bar");
        ras.add(2, "baz");
        ras.add(3, "blarg");
        ras.add(4, "a");
        assertThat(Arrays.equals(
                ras.toArray(),
                new String [] {"foo", "bar", "baz", "blarg", "a"}));

        // Inspect the blocks:
        assertEqual(ras.blocks.size(), 3);
        assertThat(Arrays.equals(
                ras.blocks.get(0),
                new String[] {"foo"}));
        assertThat(Arrays.equals(
                ras.blocks.get(1),
                new String[] {"bar", "baz"}));
        assertThat(Arrays.equals(
                ras.blocks.get(2),
                new String[] {"blarg", "a", null}));

        // Test growth:
        ras.add(5, "b");
        assertThat(Arrays.equals(
                ras.blocks.get(2),
                new String[] {"blarg", "a", "b"}));
        // Block count still is the same, but now we're full.
        assertEqual(ras.blocks.size(), 3);
        ras.add(6, "c");
        // Block count has increased:
        assertEqual(ras.blocks.size(), 4);
        assertThat(Arrays.equals(
                ras.blocks.get(3),
                new String[] {"c", null, null, null}));
    }

    @Test
    public void testAdd_insert() {
        ras = new RootishArrayStack<>(3, String.class);
        ras.add(0, "foo");
        // Insert a value, which means we need to shift other values.
        ras.add(0, "bar");
        assertThat(Arrays.equals(
                ras.toArray(),
                new String[] {"bar", "foo"}));
    }

    @Test
    public void testSet() {
        ras.add(0, "foo");
        ras.add(1, "bar");
        assertThat(Arrays.equals(
                ras.toArray(),
                new String [] {"foo", "bar"}));
        ras.set(1, "baz");
        assertThat(Arrays.equals(
                ras.toArray(),
                new String [] {"foo", "baz"}));

    }

    @Test
    public void testSet_outOfBounds() {
        // Empty collection: index 0
        try {
            ras.set(0, "foo");
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }

        // Negative index: index -1
        try {
            ras.set(-1, "foo");
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }

        ras.add(0, "foo");
        // Non-empty collection: index 1
        try {
            ras.set(1, "foo");
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testGet() {
        // Test boundary: 0 (with empty collection)
        try {
            ras.get(0);
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }

        ras.add(0, "foo");
        assertEqual(ras.get(0), "foo");
        ras.add(0, "bar");
        assertEqual(ras.get(1), "foo");
        assertEqual(ras.get(0), "bar");

        // Test boundary: 2 (index out of range)
        try {
            ras.get(2);
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }
        // Test boundary: -1
        try {
            ras.get(-1);
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testRemove() {
        ras = new RootishArrayStack<>(3, String.class);
        ras.add(0, "foo");
        ras.add(1, "bar");
        ras.add(2, "baz");

        // Sanity checks:
        assertEqual(ras.blocks.size(), 2);
        assertThat(Arrays.equals(
                ras.blocks.get(0),
                new String[] {"foo"}));
        assertThat(Arrays.equals(
                ras.blocks.get(1),
                new String[] {"bar", "baz"}));

        // Add one more element, collection should grow
        ras.add(3, "blarg");
        assertEqual(ras.blocks.size(), 3);
        assertThat(Arrays.equals(
                ras.blocks.get(2),
                new String[] {"blarg", null, null}));

        // - Remove element at index 1
        // - Test that the correct value is returned
        // - Everything should shift toward the left
        // - The collection will not shrink until we remove more elements
        String value = ras.remove(1);
        assertEqual(value, "bar");
        assertEqual(ras.blocks.size(), 3);
        assertThat(Arrays.equals(
                ras.blocks.get(0),
                new String[] {"foo"}));
        assertThat(Arrays.equals(
                ras.blocks.get(1),
                new String[] {"baz", "blarg"}));
        assertEqual(ras.size(), 3);

        // Remove more elements, to trigger shrinking:
        ras.remove(0);
        assertEqual(ras.blocks.size(), 3);
        ras.remove(1);
        assertEqual(ras.blocks.size(), 2);
        assertThat(Arrays.equals(
                ras.blocks.get(0),
                new String [] {"baz"}));
        assertThat(Arrays.equals(
                ras.blocks.get(1),
                // These "blarg" strings are still in memory...
                new String [] {"blarg", "blarg"}));
        // .. but they're not accessible
        try {
            ras.get(1);
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }

    }

    @Test
    public void testRemove_outOfBounds() {
        // Empty collection: index 0
        try {
            ras.remove(0);
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }

        // Negative index: index -1
        try {
            ras.remove(-1);
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }

        ras.add(0, "foo");
        // Non-empty collection: index 1
        try {
            ras.remove(1);
            fail();
        }
        catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public static void main(String [] args) {
        new RootishArrayStackTest().runTests(true);
    }
}
