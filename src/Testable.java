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

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Testable {

    /**
     * Simple test annotation, to indicate that a method is a test method.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Test {}

    public class TestItFailure extends RuntimeException {
        public TestItFailure() {}
        public TestItFailure(String message) {
            super(message);
        }
    }

    public void assertThat(boolean expr) throws TestItFailure {
        if (!expr) {
            throw new TestItFailure();
        }
    }

    public void assertEqual(Object a, Object b) throws TestItFailure {
        if (!a.equals(b)) {
            throw new TestItFailure(String.format(
                    "%s != %s",
                    a.toString(),
                    b.toString()));
        }
    }

    public void fail() {
        assertThat(false);
    }

    /**
     * Default `runTests` method; `verbose` defaults to false.
     * See below.
     */
    public void runTests() {
        runTests(false);
    }

    /**
     *
     * @param verbose: if true, print stack traces of errors to stderr;
     * otherwise, just show "pass" or "fail" for each test.
     */
    public void runTests(boolean verbose) {
        try {
            int failures = doRunTests(verbose);
            if (failures > 0) {
                // If there are any failures, we should return
                // with a proper exit code indicating the failure.
                System.exit(1);
            }
        }
        catch (Exception e) {
            // Something unexpected happened.
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Override in subclasses to specify custom setup commands for each test
     * method.
     */
    public void setUp() {}

    private int doRunTests(boolean verbose) throws IllegalAccessException, IllegalArgumentException {

        int testCount = 0;
        int passCount = 0;
        int failCount = 0;

        for (Method meth: this.getClass().getDeclaredMethods()) {
            // Check for `ATest` annotations, in order to find test methods.
            Annotation[] annots = meth.getAnnotationsByType(Testable.Test.class);
            if (annots.length > 0) {
                testCount++;
                boolean pass = true;
                try {
                    this.setUp();
                    meth.invoke(this);
                }
                catch (InvocationTargetException ex) {
                    if (verbose) {
                        System.err.println("Error in method: " + meth.getName());
                        ex.getTargetException().printStackTrace();
                    }
                    pass = false;
                }

                if (pass) {
                    passCount++;
                }
                else {
                    failCount++;
                }
                System.out.printf("%s: %s\n", meth.getName(), pass ? "pass": "fail");
            }
        }
        System.out.printf(
                "%d/%d tests ran successfully (failed: %d)\n",
                passCount,
                testCount,
                failCount);
        return failCount;
    }

}
