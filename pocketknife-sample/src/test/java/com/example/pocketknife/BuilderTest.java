package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml")
public class BuilderTest {

    private Builder builder;
    private Random random;

    @Before
    public void setup() {
        random = new Random(42);
        builder = new PocketKnifeBuilder(RuntimeEnvironment.application);
    }

    @Test
    public void testGetBundle() throws Exception {
        int i = random.nextInt();
        int j = random.nextInt();
        Bundle bundle = builder.getBundle(i, j);
        assertEquals(i, bundle.getInt(PocketKnifeBuilder.ARG_ARG, (i << 1) | 1));
        assertEquals(j, bundle.getInt(PocketKnifeBuilder.ARG_TEST, (j << 1) | 1));
    }

    @Test
    public void testGetIntent() throws Exception {
        int i = random.nextInt();
        String s = "This is a test";
        Intent intent = builder.getIntent(i, s);
        assertEquals(Intent.ACTION_DEFAULT, intent.getAction());
        assertEquals(i, intent.getIntExtra(PocketKnifeBuilder.EXTRA_EXTRA, i << 1));
        assertEquals(s, intent.getStringExtra(PocketKnifeBuilder.EXTRA_TEXT));
    }

    @Test
    public void testGetFragment() throws Exception {
        int i = random.nextInt();
        ParentFragment fragment = builder.getFragment(i);
        assertNotNull("Fragment", fragment);
        Bundle args = fragment.getArguments();
        assertNotNull("Args", args);
        assertEquals(i, args.getInt(PocketKnifeBuilder.ARG_PARENT_ARG, i << 1));

    }

    @After
    public void teardown() {
        random = null;
        builder = null;
    }
}