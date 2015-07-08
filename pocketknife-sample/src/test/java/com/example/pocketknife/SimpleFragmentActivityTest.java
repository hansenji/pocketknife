package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml")
public class SimpleFragmentActivityTest extends BaseTest {

    @Test
    public void verifyArgumentBinding() {
        Bundle bundle = new Bundle();
        Random random = new Random(42);

        boolean aBoolean = random.nextBoolean();
        bundle.putBoolean(SimpleFragment.BOOLEAN, aBoolean);
        boolean[] booleans = {random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean()};
        bundle.putBooleanArray(SimpleFragment.BOOLEAN_ARRAY, booleans);

        Bundle otherBundle = new Bundle();
        otherBundle.putInt(BUNDLE_INT_ARG, random.nextInt());
        otherBundle.putSerializable(BUNDLE_SERIALIZABLE_ARG, UUID.randomUUID().toString());
        bundle.putBundle(SimpleFragment.BUNDLE, otherBundle);

        byte aByte = (byte)random.nextInt();
        bundle.putByte(SimpleFragment.BYTE, aByte);
        byte[] bytes = new byte[random.nextInt(10)];
        random.nextBytes(bytes);
        bundle.putByteArray(SimpleFragment.BYTE_ARRAY, bytes);

        char aChar = (char)random.nextInt();
        bundle.putChar(SimpleFragment.CHAR, aChar);
        char[] chars = {(char)random.nextInt(), (char)random.nextInt()};
        bundle.putCharArray(SimpleFragment.CHAR_ARRAY, chars);

        CharSequence charSequence = UUID.randomUUID().toString();
        bundle.putCharSequence(SimpleFragment.CHAR_SEQUENCE, charSequence);
        CharSequence[] charSequences = {UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()};
        bundle.putCharSequenceArray(SimpleFragment.CHAR_SEQUENCE_ARRAY, charSequences);
        ArrayList<CharSequence> charSequenceArrayList = new ArrayList<CharSequence>();
        charSequenceArrayList.add(UUID.randomUUID().toString());
        bundle.putCharSequenceArrayList(SimpleFragment.CHAR_SEQUENCE_ARRAY_LIST, charSequenceArrayList);

        double aDouble = random.nextDouble();
        bundle.putDouble(SimpleFragment.DOUBLE, aDouble);
        double[] doubles = {random.nextDouble()};
        bundle.putDoubleArray(SimpleFragment.DOUBLE_ARRAY, doubles);

        float aFloat = random.nextFloat();
        bundle.putFloat(SimpleFragment.FLOAT, aFloat);
        float[] floats = {random.nextFloat()};
        bundle.putFloatArray(SimpleFragment.FLOAT_ARRAY, floats);

        int anInt = random.nextInt();
        bundle.putInt(SimpleFragment.INT, anInt);
        int[] ints = {random.nextInt(), random.nextInt(), random.nextInt(), random.nextInt()};
        bundle.putIntArray(SimpleFragment.INT_ARRAY, ints);
        ArrayList<Integer> integerArrayList = new ArrayList<Integer>();
        integerArrayList.add(random.nextInt());
        bundle.putIntegerArrayList(SimpleFragment.INTEGER_ARRAY_LIST, integerArrayList);

        long aLong = random.nextLong();
        bundle.putLong(SimpleFragment.LONG, aLong);
        long[] longs = {random.nextLong(), random.nextLong(), random.nextLong(), random.nextLong()};
        bundle.putLongArray(SimpleFragment.LONG_ARRAY, longs);

        MyParcelable parcelable = new MyParcelable(random.nextInt());
        bundle.putParcelable(SimpleFragment.PARCELABLE, parcelable);
        MyParcelable[] parcelables = {new MyParcelable(random.nextInt()), new MyParcelable(random.nextInt()), new MyParcelable(random.nextInt())};
        bundle.putParcelableArray(SimpleFragment.PARCELABLE_ARRAY, parcelables);
        ArrayList<MyParcelable> parcelableArrayList = new ArrayList<MyParcelable>();
        parcelableArrayList.add(new MyParcelable(random.nextInt()));
        bundle.putParcelableArrayList(SimpleFragment.PARCELABLE_ARRAY_LIST, parcelableArrayList);

        Serializable serializable = UUID.randomUUID().toString();
        bundle.putSerializable(SimpleFragment.SERIALIZABLE, serializable);

        short aShort = (short)random.nextInt();
        bundle.putShort(SimpleFragment.SHORT, aShort);
        short[] shorts = {(short)random.nextInt(), (short)random.nextInt(), (short)random.nextInt(), (short)random.nextInt()};
        bundle.putShortArray(SimpleFragment.SHORT_ARRAY, shorts);

        String string = UUID.randomUUID().toString();
        bundle.putString(SimpleFragment.STRING, string);
        String[] strings = {UUID.randomUUID().toString()};
        bundle.putStringArray(SimpleFragment.STRING_ARRAY, strings);
        ArrayList<String> stringArrayList = new ArrayList<String>();
        stringArrayList.add(UUID.randomUUID().toString());
        bundle.putStringArrayList(SimpleFragment.STRING_ARRAY_LIST, stringArrayList);

        bundle.putString(Intent.EXTRA_TEXT, string);

        ActivityController<SimpleFragmentActivity> initialController = Robolectric.buildActivity(SimpleFragmentActivity.class).create();
        SimpleFragmentActivity simpleActivity = initialController.start().restart().visible().get();
        SimpleFragment simpleFragment = SimpleFragment.newInstance();
        simpleFragment.setArguments(bundle);
        simpleActivity.replaceFragment(simpleFragment);

        // Boolean
        assertEquals(aBoolean, simpleFragment.aBoolean);
        // Array
        assertBooleanArrayEquals(booleans, simpleFragment.booleans);

        // Bundle
        assertBundleEquals(otherBundle, simpleFragment.bundle);

        // Byte
        assertEquals(aByte, simpleFragment.aByte);
        // Array
        assertArrayEquals(bytes, simpleFragment.bytes);

        // Char
        assertEquals(aChar, simpleFragment.aChar);
        // Array
        assertArrayEquals(chars, simpleFragment.chars);

        // CharSequence
        assertEquals(charSequence, simpleFragment.charSequence);
        // Array
        assertArrayEquals(charSequences, simpleFragment.charSequences);
        // ArrayList
        assertArrayListEquals(charSequenceArrayList, simpleFragment.charSequenceArrayList);

        // Double
        assertEquals(aDouble, simpleFragment.aDouble, 0);
        // Array
        assertArrayEquals(doubles, simpleFragment.doubles, 0);

        // Float
        assertEquals(aFloat, simpleFragment.aFloat, 0);
        // Array
        assertArrayEquals(floats, simpleFragment.floats, 0);

        // Int
        assertEquals(anInt, simpleFragment.anInt);
        // Array
        assertArrayEquals(ints, simpleFragment.ints);
        // ArrayList
        assertArrayListEquals(integerArrayList, simpleFragment.integerArrayList);

        // Long
        assertEquals(aLong, simpleFragment.aLong);
        // Array
        assertArrayEquals(longs, simpleFragment.longs);

        //Parcelable
        assertEquals(parcelable, simpleFragment.parcelable);
        // Array
        assertArrayEquals(parcelables, simpleFragment.parcelables);
        // ArrayList
        assertArrayListEquals(parcelableArrayList, simpleFragment.parcelableArrayList);

        // Serializable
        String s1 = (String) serializable;
        String s2 = (String)simpleFragment.serializable;
        assertEquals(s1, s2);

        // Shorts
        assertEquals(aShort, simpleFragment.aShort);
        // Array
        assertArrayEquals(shorts, simpleFragment.shorts);

        // String
        assertEquals(string, simpleFragment.string);
        // Array
        assertArrayEquals(strings, simpleFragment.strings);
        // ArrayList
        assertArrayListEquals(stringArrayList, simpleFragment.stringArrayList);

        // Not Required
        assertNull(simpleFragment.s);
        assertEquals(SimpleActivity.NRI_DEFAULT, simpleFragment.notRequired);

        assertEquals(string, simpleFragment.text);

    }

    @Test (expected = IllegalStateException.class)
    public void verifyNullBundleException() {
        ActivityController<SimpleFragmentActivity> initialController = Robolectric.buildActivity(SimpleFragmentActivity.class).create();
        SimpleFragmentActivity simpleActivity = initialController.start().restart().visible().get();
        SimpleFragment simpleFragment = SimpleFragment.newInstance();
        simpleActivity.replaceFragment(simpleFragment);
    }

    @Test (expected = IllegalStateException.class)
    public void verifyArgumentBindingException() {
        Bundle bundle = new Bundle();
        ActivityController<SimpleFragmentActivity> initialController = Robolectric.buildActivity(SimpleFragmentActivity.class).create();
        SimpleFragmentActivity simpleActivity = initialController.start().restart().visible().get();
        SimpleFragment simpleFragment = SimpleFragment.newInstance();
        simpleFragment.setArguments(bundle);
        simpleActivity.replaceFragment(simpleFragment);
    }

    @Test
    public void verifyNotRequiredArgumentBinding() {
        ActivityController<SimpleFragmentActivity> initialController = Robolectric.buildActivity(SimpleFragmentActivity.class).create();
        SimpleFragmentActivity simpleActivity = initialController.start().restart().visible().get();
        NotRequiredArgFragment fragment = new NotRequiredArgFragment();
        simpleActivity.replaceFragment(fragment);

        assertEquals(1, fragment.i);
        assertEquals("NOT_REQUIRED", fragment.s);
    }

}
