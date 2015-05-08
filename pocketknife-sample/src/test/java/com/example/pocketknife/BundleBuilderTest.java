package com.example.pocketknife;

import android.os.Bundle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml")
public class BundleBuilderTest extends BaseTest {

    private Bundles bundles;

    @Before
    public void setup() {
        bundles = new PocketKnifeBundles();
    }

    @After
    public void tearDown() {
        bundles = null;
    }

    @Test
    public void testExtras() throws Exception {
        Random random = new Random(42);
        int i = random.nextInt();
        int j = random.nextInt();
        Bundle bundle = bundles.getSingleArgBundle(i);
        assertEquals("EXTRA", i, bundle.getInt(PocketKnifeBundles.ARG_I, 0));
        bundle = bundles.getEmptyBundle();
        assertEquals(0, bundle.size());

        bundle = bundles.getTwinArgsBundle(i, j);
        assertEquals("SAME TYPE I", i, bundle.getInt(PocketKnifeBundles.ARG_I, 0));
        assertEquals("SAME TYPE J", j, bundle.getInt(PocketKnifeBundles.ARG_J, 0));

        bundle = bundles.getNameCollisionBundle(0, 1, 2);
        assertEquals("NAME COLLISION", 0, bundle.getInt(PocketKnifeBundles.ARG_BUNDLE, 0));
        assertEquals("NAME COLLISION", 1, bundle.getInt(PocketKnifeBundles.ARG_BUNDLE1, 0));
        assertEquals("NAME COLLISION", 2, bundle.getInt(PocketKnifeBundles.ARG_BUNDLE2, 0));

        boolean aBoolean = random.nextBoolean();
        boolean[] booleans = {random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean()};
        Bundle bundle1 = new Bundle();
        bundle1.putInt(BUNDLE_INT_ARG, random.nextInt());
        bundle1.putSerializable(BUNDLE_SERIALIZABLE_ARG, UUID.randomUUID().toString());
        byte aByte = (byte) random.nextInt();
        byte[] bytes = new byte[random.nextInt(10)];
        random.nextBytes(bytes);
        char aChar = (char) random.nextInt();
        char[] chars = {(char) random.nextInt(), (char) random.nextInt()};
        CharSequence charSequence = UUID.randomUUID().toString();
        CharSequence[] charSequences = {UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()};
        ArrayList<CharSequence> charSequenceArrayList = new ArrayList<CharSequence>();
        charSequenceArrayList.add(UUID.randomUUID().toString());
        double aDouble = random.nextDouble();
        double[] doubles = {random.nextDouble()};
        float aFloat = random.nextFloat();
        float[] floats = {random.nextFloat()};
        int anInt = random.nextInt();
        int[] ints = {random.nextInt(), random.nextInt(), random.nextInt(), random.nextInt()};
        ArrayList<Integer> integerArrayList = new ArrayList<Integer>();
        integerArrayList.add(random.nextInt());
        long aLong = random.nextLong();
        long[] longs = {random.nextLong(), random.nextLong(), random.nextLong(), random.nextLong()};
        MyParcelable parcelable = new MyParcelable(random.nextInt());
        MyParcelable[] parcelables = {new MyParcelable(random.nextInt()), new MyParcelable(random.nextInt()), new MyParcelable(random.nextInt())};
        ArrayList<MyParcelable> parcelableArrayList = new ArrayList<MyParcelable>();
        parcelableArrayList.add(new MyParcelable(random.nextInt()));
        Serializable serializable = UUID.randomUUID().toString();
        short aShort = (short) random.nextInt();
        short[] shorts = {(short) random.nextInt(), (short) random.nextInt(), (short) random.nextInt(), (short) random.nextInt()};
        String string = UUID.randomUUID().toString();
        String[] strings = {UUID.randomUUID().toString()};
        ArrayList<String> stringArrayList = new ArrayList<String>();
        stringArrayList.add(UUID.randomUUID().toString());
        bundle = bundles.getBundle(aBoolean, booleans, bundle1, aByte, bytes, aChar, chars, charSequence, charSequences, charSequenceArrayList, aDouble, doubles,
                aFloat, floats, anInt, ints, integerArrayList, aLong, longs, parcelable, parcelables, parcelableArrayList, serializable, aShort,
                shorts, string, strings, stringArrayList);

        assertEquals(aBoolean, bundle.getBoolean(PocketKnifeBundles.ARG_A_BOOLEAN, false));
        assertArrayEquals(booleans, bundle.getBooleanArray(PocketKnifeBundles.ARG_BOOLEANS));
        assertBundleEquals(bundle1, bundle.getBundle(PocketKnifeBundles.ARG_BUNDLE));
        assertEquals(aByte, bundle.getByte(PocketKnifeBundles.ARG_A_BYTE));
        assertArrayEquals(bytes, bundle.getByteArray(PocketKnifeBundles.ARG_BYTES));
        assertEquals(aChar, bundle.getChar(PocketKnifeBundles.ARG_A_CHAR, '0'));
        assertArrayEquals(chars, bundle.getCharArray(PocketKnifeBundles.ARG_CHARS));
        assertEquals(charSequence, bundle.getCharSequence(PocketKnifeBundles.ARG_CHAR_SEQUENCE));
        assertArrayEquals(charSequences, bundle.getCharSequenceArray(PocketKnifeBundles.ARG_CHAR_SEQUENCES));
        assertArrayListEquals(charSequenceArrayList, bundle.getCharSequenceArrayList(PocketKnifeBundles.ARG_CHAR_SEQUENCE_ARRAY_LIST));
        assertEquals(aDouble, bundle.getDouble(PocketKnifeBundles.ARG_A_DOUBLE, 0.0), 0);
        assertArrayEquals(doubles, bundle.getDoubleArray(PocketKnifeBundles.ARG_DOUBLES), 0);
        assertEquals(aFloat, bundle.getFloat(PocketKnifeBundles.ARG_A_FLOAT, 0.0f), 0);
        assertArrayEquals(floats, bundle.getFloatArray(PocketKnifeBundles.ARG_FLOATS), 0);
        assertEquals(anInt, bundle.getInt(PocketKnifeBundles.ARG_AN_INT, 0));
        assertArrayEquals(ints, bundle.getIntArray(PocketKnifeBundles.ARG_INTS));
        assertArrayListEquals(integerArrayList, bundle.getIntegerArrayList(PocketKnifeBundles.ARG_INTEGER_ARRAY_LIST));
        assertEquals(aLong, bundle.getLong(PocketKnifeBundles.ARG_A_LONG, 0));
        assertArrayEquals(longs, bundle.getLongArray(PocketKnifeBundles.ARG_LONGS));
        assertEquals(parcelable, bundle.getParcelable(PocketKnifeBundles.ARG_PARCELABLE));
        assertArrayEquals(parcelables, bundle.getParcelableArray(PocketKnifeBundles.ARG_PARCELABLES));
        assertArrayListEquals(parcelableArrayList, bundle.getParcelableArrayList(PocketKnifeBundles.ARG_PARCELABLE_ARRAY_LIST));
        assertEquals(serializable, bundle.getSerializable(PocketKnifeBundles.ARG_SERIALIZABLE));
        assertEquals(aShort, bundle.getShort(PocketKnifeBundles.ARG_A_SHORT, (short) 0));
        assertArrayEquals(shorts, bundle.getShortArray(PocketKnifeBundles.ARG_SHORTS));
        assertEquals(string, bundle.getString(PocketKnifeBundles.ARG_STRING));
        assertArrayEquals(strings, bundle.getStringArray(PocketKnifeBundles.ARG_STRINGS));
        assertArrayListEquals(stringArrayList, bundle.getStringArrayList(PocketKnifeBundles.ARG_STRING_ARRAY_LIST));
    }
}
