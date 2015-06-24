package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
        assertEquals(j, bundle.getInt("THIS IS A TEST", (j << 1) | 1));
    }

    @Test
    public void testGetIntent() throws Exception {
        int i = random.nextInt();
        String s = "This is a test";
        Intent intent = builder.getIntent(i, s);
        assertEquals(Intent.ACTION_DEFAULT, intent.getAction());
        assertEquals(i, intent.getIntExtra(PocketKnifeBuilder.EXTRA_EXTRA, i << 1));
        assertEquals(s, intent.getStringExtra(Intent.EXTRA_TEXT));
    }

    @Test
    public void testGetFragment() throws Exception {
        int i = random.nextInt();
        Fragment fragment = builder.getFragment(i);
        assertNotNull("Fragment", fragment);
        Bundle args = fragment.getArguments();
        assertNotNull("Args", args);
        assertEquals(i, args.getInt(PocketKnifeBuilder.ARG_ARG, i << 1));

    }

    @Test
    public void testKeyNameConflict() {
        int i = random.nextInt();
        int j = random.nextInt();
        int k = random.nextInt();
        Bundle b1 = builder.getBundle(i);
        Bundle b2 = builder.getBundle(j, k);
        assertEquals(i, b1.getInt("Testing is awesome", (i << i) | 1));
        assertEquals(j, b2.getInt(PocketKnifeBuilder.ARG_ARG, (j << 1) | 1));
        assertEquals(k, b2.getInt("THIS IS A TEST", (k << 1) | 1));

        Fragment f1 = builder.getFragment(i);
        assertNotNull("Fragment", f1);
        Bundle args1 = f1.getArguments();
        assertNotNull("Args", args1);
        assertEquals(i, args1.getInt(PocketKnifeBuilder.ARG_ARG, i << 1));

        Fragment f2 = builder.getFragment(j, k);
        assertNotNull("Fragment 2", f2);
        Bundle args2 = f2.getArguments();
        assertNotNull("Args 2", args2);
        assertEquals(j, args2.getInt("Fragment Test", j << 1));
        assertEquals(k, args2.getInt(PocketKnifeBuilder.ARG_ARG2, k << 1));

        String text1 = "Text 1";
        String text2 = "Text 2";
        Intent i1 = builder.getIntent(i, text1);
        assertEquals(Intent.ACTION_DEFAULT, i1.getAction());
        assertEquals(i, i1.getIntExtra(PocketKnifeBuilder.EXTRA_EXTRA, i << 1));
        assertEquals(text1, i1.getStringExtra(Intent.EXTRA_TEXT));

        Intent i2 = builder.getIntent(text2);
        assertEquals(Intent.ACTION_DEFAULT, i2.getAction());
        assertEquals(text2, i2.getStringExtra(PocketKnifeBuilder.EXTRA_TEXT));
    }

    @After
    public void teardown() {
        random = null;
        builder = null;
    }
}