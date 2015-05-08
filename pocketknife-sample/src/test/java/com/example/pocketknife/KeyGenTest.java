package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml")
public class KeyGenTest extends BaseTest {

    @Test
    public void testExtraKeyGeneration() {
        String s = UUID.randomUUID().toString();
        int i = new Random(42).nextInt();
        Intent intent = new Intent(RuntimeEnvironment.application, KeyGenActivity.class);
        intent.putExtra("EXTRA_STRING", s);
        intent.putExtra("EXTRA_BLAH", i);

        KeyGenActivity keyGenActivity = Robolectric.buildActivity(KeyGenActivity.class).withIntent(intent).create().start().restart().visible().get();

        assertEquals(s, keyGenActivity.string);
        assertEquals(i, keyGenActivity.blah);
    }

    @Test
    public void testArgKeyGeneration() {
        String s = UUID.randomUUID().toString();
        int i = new Random(42).nextInt();

        Bundle bundle = new Bundle();
        bundle.putString("ARG_A_STRING", s);
        bundle.putInt("ARG_AN_INT", i);

        ActivityController<SimpleFragmentActivity> initialController = Robolectric.buildActivity(SimpleFragmentActivity.class).create();
        SimpleFragmentActivity simpleActivity = initialController.start().restart().visible().get();
        KeyGenFragment fragment = KeyGenFragment.newInstance();
        fragment.setArguments(bundle);
        simpleActivity.replaceFragment(fragment);

        assertEquals(s, fragment.aString);
        assertEquals(i, fragment.anInt);
    }

}