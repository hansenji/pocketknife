package com.example.pocketknife;

import android.os.Bundle;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BaseTest {
    protected static final String BUNDLE_INT_ARG = "BUNDLE_INT_ARG";
    protected static final String BUNDLE_SERIALIZABLE_ARG = "BUNDLE_SERIALIZABLE_ARG";

    protected static void assertBundleEquals(Bundle expected, Bundle actual) {
        if (expected == null) {
            assertNull(actual);
        } else {
            assertNotNull(actual);
            assertEquals(expected.getInt(BUNDLE_INT_ARG), actual.getInt(BUNDLE_INT_ARG));
            assertEquals(expected.getSerializable(BUNDLE_SERIALIZABLE_ARG), actual.getSerializable(BUNDLE_SERIALIZABLE_ARG));
        }
    }

    protected static void assertBooleanArrayEquals(boolean[] expected, boolean[] actual) {
        if (expected == null) {
            assertNull(actual);
        } else {
            assertNotNull(actual);
            assertEquals(expected.length, actual.length);
            int count = expected.length;
            for (int i = 0; i < count; i++) {
                assertEquals("Arrays not equals for item: " + i, expected[i], actual[i]);
            }
        }
    }

    protected static void assertArrayListEquals(ArrayList<?> expected, ArrayList<?> actual) {
        if (expected == null) {
            assertNull(actual);
        } else {
            assertNotNull(actual);
            assertEquals(expected.size(), actual.size());
            int count = expected.size();
            for (int i = 0; i < count; i++) {
                assertEquals("ArrayLists not equals for item: " + i, expected.get(i), actual.get(i));
            }
        }
    }
}
