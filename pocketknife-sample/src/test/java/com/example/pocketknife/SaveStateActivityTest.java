package com.example.pocketknife;

import android.os.Bundle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml")
public class SaveStateActivityTest extends BaseTest {

    @Test
    public void verifySaveRestoreState() {
        Random random = new Random(42);

        ActivityController<SaveStateActivity> initialController = Robolectric.buildActivity(SaveStateActivity.class).create();
        SaveStateActivity original = initialController.start().restart().visible().get();

        original.aBoolean = random.nextBoolean();
        original.booleans = new boolean[]{random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean()};

        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_INT_ARG, random.nextInt());
        bundle.putSerializable(BUNDLE_SERIALIZABLE_ARG, UUID.randomUUID().toString());

        original.aByte = (byte)random.nextInt();
        original.bytes = new byte[random.nextInt(10)];
        random.nextBytes(original.bytes);

        original.aChar = (char)random.nextInt();
        original.chars = new char[]{(char)random.nextInt(), (char)random.nextInt()};


        original.charSequence = UUID.randomUUID().toString();

        original.charSequences = new CharSequence[]{UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()};

        original.charSequenceArrayList = new ArrayList<CharSequence>();
        original.charSequenceArrayList.add(UUID.randomUUID().toString());

        original.aDouble = random.nextDouble();
        original.doubles = new double[]{random.nextDouble()};

        original.aFloat = random.nextFloat();
        original.floats = new float[]{random.nextFloat()};

        original.anInt = random.nextInt();
        original.ints = new int[]{random.nextInt(), random.nextInt(), random.nextInt(), random.nextInt()};

        original.integerArrayList = new ArrayList<Integer>();
        original.integerArrayList.add(random.nextInt());

        original.aLong = random.nextLong();
        original.longs = new long[]{random.nextLong(), random.nextLong(), random.nextLong(), random.nextLong()};


        original.parcelable = new MyParcelable(random.nextInt());
        original.parcelables = new MyParcelable[]{new MyParcelable(random.nextInt()), new MyParcelable(random.nextInt()), new MyParcelable(random.nextInt())};
        original.parcelableArrayList = new ArrayList<MyParcelable>();
        original.parcelableArrayList.add(new MyParcelable(random.nextInt()));


        original.serializable = UUID.randomUUID().toString();

        original.aShort = (short)random.nextInt();
        original.shorts = new short[]{(short)random.nextInt(), (short)random.nextInt(), (short)random.nextInt(), (short)random.nextInt()};

        original.string = UUID.randomUUID().toString();
        original.strings = new String[]{UUID.randomUUID().toString()};
        original.stringArrayList = new ArrayList<String>();
        original.stringArrayList.add(UUID.randomUUID().toString());

        Bundle saveState = new Bundle();
        initialController.saveInstanceState(saveState);
        ActivityController<SaveStateActivity> secondaryController = Robolectric.buildActivity(SaveStateActivity.class).create(saveState);
        SaveStateActivity activity = secondaryController.start().restart().visible().get();

        // Boolean
        assertEquals(original.aBoolean, activity.aBoolean);
        // Array
        assertBooleanArrayEquals(original.booleans, activity.booleans);

        // Bundle
        assertBundleEquals(original.bundle, activity.bundle);

        // Byte
        assertEquals(original.aByte, activity.aByte);
        // Array
        assertArrayEquals(original.bytes, activity.bytes);

        // Char
        assertEquals(original.aChar, activity.aChar);
        // Array
        assertArrayEquals(original.chars, activity.chars);

        // CharSequence
        assertEquals(original.charSequence, activity.charSequence);
        // Array
        assertArrayEquals(original.charSequences, activity.charSequences);
        // ArrayList
        assertArrayListEquals(original.charSequenceArrayList, activity.charSequenceArrayList);

        // Double
        assertEquals(original.aDouble, activity.aDouble, 0);
        // Array
        assertArrayEquals(original.doubles, activity.doubles, 0);

        // Float
        assertEquals(original.aFloat, activity.aFloat, 0);
        // Array
        assertArrayEquals(original.floats, activity.floats, 0);

        // Int
        assertEquals(original.anInt, activity.anInt);
        // Array
        assertArrayEquals(original.ints, activity.ints);
        // ArrayList
        assertArrayListEquals(original.integerArrayList, activity.integerArrayList);

        // Long
        assertEquals(original.aLong, activity.aLong);
        // Array
        assertArrayEquals(original.longs, activity.longs);

        //Parcelable
        assertEquals(original.parcelable, activity.parcelable);
        // Array
        assertArrayEquals(original.parcelables, activity.parcelables);
        // ArrayList
        assertArrayListEquals(original.parcelableArrayList, activity.parcelableArrayList);

        // Serializable
        String s1 = (String)original.serializable;
        String s2 = (String)activity.serializable;
        assertEquals(s1, s2);

        // Shorts
        assertEquals(original.aShort, activity.aShort);
        // Array
        assertArrayEquals(original.shorts, activity.shorts);

        // String
        assertEquals(original.string, activity.string);
        // Array
        assertArrayEquals(original.strings, activity.strings);
        // ArrayList
        assertArrayListEquals(original.stringArrayList, activity.stringArrayList);

    }

    @Test (expected = IllegalStateException.class)
    public void verifySaveRestoreExceptionThrown() {
        Robolectric.buildActivity(SaveStateActivity.class).create(new Bundle());
    }

    @Test
    public void verifyNotRequiredSaveRestoreState() {
        Robolectric.buildActivity(NotRequiredSaveStateActivity.class).create(new Bundle()).get();
    }
}
