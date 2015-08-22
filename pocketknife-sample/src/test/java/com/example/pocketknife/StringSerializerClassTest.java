package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import pocketknife.PocketKnife;

import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml")
public class StringSerializerClassTest {

    @Test
    public void testSaveState() {
        String s = UUID.randomUUID().toString();
        StringSerializerClass stringSerializerClass1 = new StringSerializerClass();
        stringSerializerClass1.saveString = s;

        Bundle bundle = new Bundle();
        PocketKnife.saveInstanceState(stringSerializerClass1, bundle);
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            assertNotEquals(s, bundle.getString(key));
        }
        StringSerializerClass stringSerializerClass2 = new StringSerializerClass();
        PocketKnife.restoreInstanceState(stringSerializerClass2, bundle);
        assertEquals(s, stringSerializerClass2.saveString);
    }

    @Test
    public void testBuildAndBindArgument() {
        String s = UUID.randomUUID().toString();
        StringSerializerClass stringSerializerClass = new StringSerializerClass();
        SerializerBuilder builder = new PocketKnifeSerializerBuilder(RuntimeEnvironment.application);
        Bundle bundle = builder.buildBundle(s);

        assertNotEquals(s, bundle.getString(PocketKnifeSerializerBuilder.ARG_ARG_STRING));
        PocketKnife.bindArguments(stringSerializerClass, bundle);
        assertEquals(s, stringSerializerClass.argString);
    }

    @Test
    public void testBuildAndBindExtras() {
        String s = UUID.randomUUID().toString();
        StringSerializerClass stringSerializerClass = new StringSerializerClass();
        SerializerBuilder builder = new PocketKnifeSerializerBuilder(RuntimeEnvironment.application);
        Intent intent = builder.buildIntent(s);

        assertNotEquals(s, intent.getStringExtra(PocketKnifeSerializerBuilder.EXTRA_EXTRA_STRING));
        PocketKnife.bindExtras(stringSerializerClass, intent);
        assertEquals(s, stringSerializerClass.extraString);
    }

    @Test
    public void testBuildFragmentAndBindArgument() {
        String s = UUID.randomUUID().toString();
        StringSerializerClass stringSerializerClass = new StringSerializerClass();
        SerializerBuilder builder = new PocketKnifeSerializerBuilder(RuntimeEnvironment.application);
        Fragment fragment = builder.buildFragment(s);
        Bundle bundle = fragment.getArguments();

        assertNotEquals(s, bundle.getString(PocketKnifeSerializerBuilder.ARG_ARG_STRING));
        PocketKnife.bindArguments(stringSerializerClass, bundle);
        assertEquals(s, stringSerializerClass.argString);
    }
}