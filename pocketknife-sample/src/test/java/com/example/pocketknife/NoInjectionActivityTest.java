package com.example.pocketknife;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml")
public class NoInjectionActivityTest {

    @Test
    public void testNoInjection() {
        Robolectric.buildActivity(NoInjectionActivity.class).create().get();
    }
}
