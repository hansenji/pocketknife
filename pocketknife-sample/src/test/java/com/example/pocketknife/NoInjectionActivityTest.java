package com.example.pocketknife;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class NoInjectionActivityTest {

    @Test
    public void testNoInjection() {
        Robolectric.buildActivity(NoInjectionActivity.class).create().get();
    }
}
