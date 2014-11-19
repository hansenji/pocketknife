package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class SimpleActivityTest extends BaseTest {

    @Test
    public void verifyExtraInjection() {
        Intent intent = new Intent(Robolectric.application, SimpleActivity.class);
        Random random = new Random(42);

        boolean aBoolean = random.nextBoolean();
        intent.putExtra(SimpleActivity.BOOLEAN_EXTRA, aBoolean);
        boolean[] booleans = {random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean()};
        intent.putExtra(SimpleActivity.BOOLEAN_ARRAY_EXTRA, booleans);

        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_INT_ARG, random.nextInt());
        bundle.putSerializable(BUNDLE_SERIALIZABLE_ARG, UUID.randomUUID().toString());
        intent.putExtra(SimpleActivity.BUNDLE_EXTRA, bundle);

        byte aByte = (byte)random.nextInt();
        intent.putExtra(SimpleActivity.BYTE_EXTRA, aByte);
        byte[] bytes = new byte[random.nextInt(10)];
        random.nextBytes(bytes);
        intent.putExtra(SimpleActivity.BYTE_ARRAY_EXTRA, bytes);

        char aChar = (char)random.nextInt();
        intent.putExtra(SimpleActivity.CHAR_EXTRA, aChar);
        char[] chars = {(char)random.nextInt(), (char)random.nextInt()};
        intent.putExtra(SimpleActivity.CHAR_ARRAY_EXTRA, chars);

        CharSequence charSequence = UUID.randomUUID().toString();
        intent.putExtra(SimpleActivity.CHAR_SEQUENCE_EXTRA, charSequence);
        CharSequence[] charSequences = {UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()};
        intent.putExtra(SimpleActivity.CHAR_SEQUENCE_ARRAY_EXTRA, charSequences);
        ArrayList<CharSequence> charSequenceArrayList = new ArrayList<CharSequence>();
        charSequenceArrayList.add(UUID.randomUUID().toString());
        intent.putCharSequenceArrayListExtra(SimpleActivity.CHAR_SEQUENCE_ARRAY_LIST_EXTRA, charSequenceArrayList);

        double aDouble = random.nextDouble();
        intent.putExtra(SimpleActivity.DOUBLE_EXTRA, aDouble);
        double[] doubles = {random.nextDouble()};
        intent.putExtra(SimpleActivity.DOUBLE_ARRAY_EXTRA, doubles);

        float aFloat = random.nextFloat();
        intent.putExtra(SimpleActivity.FLOAT_EXTRA, aFloat);
        float[] floats = {random.nextFloat()};
        intent.putExtra(SimpleActivity.FLOAT_ARRAY_EXTRA, floats);

        int anInt = random.nextInt();
        intent.putExtra(SimpleActivity.INT_EXTRA, anInt);
        int[] ints = {random.nextInt(), random.nextInt(), random.nextInt(), random.nextInt()};
        intent.putExtra(SimpleActivity.INT_ARRAY_EXTRA, ints);
        ArrayList<Integer> integerArrayList = new ArrayList<Integer>();
        integerArrayList.add(random.nextInt());
        intent.putIntegerArrayListExtra(SimpleActivity.INTEGER_ARRAY_LIST_EXTRA, integerArrayList);

        long aLong = random.nextLong();
        intent.putExtra(SimpleActivity.LONG_EXTRA, aLong);
        long[] longs = {random.nextLong(), random.nextLong(), random.nextLong(), random.nextLong()};
        intent.putExtra(SimpleActivity.LONG_ARRAY_EXTRA, longs);

        MyParcelable parcelable = new MyParcelable(random.nextInt());
        intent.putExtra(SimpleActivity.PARCELABLE_EXTRA, parcelable);
        MyParcelable[] parcelables = {new MyParcelable(random.nextInt()), new MyParcelable(random.nextInt()), new MyParcelable(random.nextInt())};
        intent.putExtra(SimpleActivity.PARCELABLE_ARRAY_EXTRA, parcelables);
        ArrayList<MyParcelable> parcelableArrayList = new ArrayList<MyParcelable>();
        parcelableArrayList.add(new MyParcelable(random.nextInt()));
        intent.putParcelableArrayListExtra(SimpleActivity.PARCELABLE_ARRAY_LIST_EXTRA, parcelableArrayList);

        Serializable serializable = UUID.randomUUID().toString();
        intent.putExtra(SimpleActivity.SERIALIZABLE_EXTRA, serializable);

        short aShort = (short)random.nextInt();
        intent.putExtra(SimpleActivity.SHORT_EXTRA, aShort);
        short[] shorts = {(short)random.nextInt(), (short)random.nextInt(), (short)random.nextInt(), (short)random.nextInt()};
        intent.putExtra(SimpleActivity.SHORT_ARRAY_EXTRA, shorts);

        String string = UUID.randomUUID().toString();
        intent.putExtra(SimpleActivity.STRING_EXTRA, string);
        String[] strings = {UUID.randomUUID().toString()};
        intent.putExtra(SimpleActivity.STRING_ARRAY_EXTRA, strings);
        ArrayList<String> stringArrayList = new ArrayList<String>();
        stringArrayList.add(UUID.randomUUID().toString());
        intent.putExtra(SimpleActivity.STRING_ARRAY_LIST_EXTRA, stringArrayList);

        SimpleActivity simpleActivity = Robolectric.buildActivity(SimpleActivity.class).withIntent(intent).create().get();

        // Boolean
        assertEquals(aBoolean, simpleActivity.aBoolean);
        // Array
        assertBooleanArrayEquals(booleans, simpleActivity.booleans);

        // Bundle
        assertBundleEquals(bundle, simpleActivity.bundle);

        // Byte
        assertEquals(aByte, simpleActivity.aByte);
        // Array
        assertArrayEquals(bytes, simpleActivity.bytes);

        // Char
        assertEquals(aChar, simpleActivity.aChar);
        // Array
        assertArrayEquals(chars, simpleActivity.chars);

        // CharSequence
        assertEquals(charSequence, simpleActivity.charSequence);
        // Array
        assertArrayEquals(charSequences, simpleActivity.charSequences);
        // ArrayList
        assertArrayListEquals(charSequenceArrayList, simpleActivity.charSequenceArrayList);

        // Double
        assertEquals(aDouble, simpleActivity.aDouble, 0);
        // Array
        assertArrayEquals(doubles, simpleActivity.doubles, 0);

        // Float
        assertEquals(aFloat, simpleActivity.aFloat, 0);
        // Array
        assertArrayEquals(floats, simpleActivity.floats, 0);

        // Int
        assertEquals(anInt, simpleActivity.anInt);
        // Array
        assertArrayEquals(ints, simpleActivity.ints);
        // ArrayList
        assertArrayListEquals(integerArrayList, simpleActivity.integerArrayList);

        // Long
        assertEquals(aLong, simpleActivity.aLong);
        // Array
        assertArrayEquals(longs, simpleActivity.longs);

        //Parcelable
        assertEquals(parcelable, simpleActivity.parcelable);
        // Array
        assertArrayEquals(parcelables, simpleActivity.parcelables);
        // ArrayList
        assertArrayListEquals(parcelableArrayList, simpleActivity.parcelableArrayList);

        // Serializable
        String s1 = (String) serializable;
        String s2 = (String)simpleActivity.serializable;
        assertEquals(s1, s2);

        // Shorts
        assertEquals(aShort, simpleActivity.aShort);
        // Array
        assertArrayEquals(shorts, simpleActivity.shorts);

        // String
        assertEquals(string, simpleActivity.string);
        // Array
        assertArrayEquals(strings, simpleActivity.strings);
        // ArrayList
        assertArrayListEquals(stringArrayList, simpleActivity.stringArrayList);

        // Not Required
        assertNull(simpleActivity.s);
        assertEquals(SimpleActivity.NRI_DEFAULT, simpleActivity.notRequired);

    }

    @Test(expected = IllegalStateException.class)
    public void verifyExceptionThrown() {
        Intent intent = new Intent(Robolectric.application, SimpleActivity.class);
        Robolectric.buildActivity(SimpleActivity.class).withIntent(intent).create().get();
    }

    @Test(expected = IllegalStateException.class)
    public void verifyNullIntentException() {
        Robolectric.buildActivity(SimpleActivity.class).create().get();
    }

}
