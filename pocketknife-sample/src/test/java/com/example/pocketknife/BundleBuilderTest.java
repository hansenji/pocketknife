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
        bundles = new PocketKnife_Bundles();
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
        assertEquals("EXTRA", i, bundle.getInt(PocketKnife_Bundles.ARG_I, 0));
        bundle = bundles.getEmptyBundle();
        assertEquals(0, bundle.size());

        bundle = bundles.getTwinArgsBundle(i, j);
        assertEquals("SAME TYPE I", i, bundle.getInt(PocketKnife_Bundles.ARG_I, 0));
        assertEquals("SAME TYPE J", j, bundle.getInt(PocketKnife_Bundles.ARG_J, 0));

        bundle = bundles.getReturnCollisionBundle(0, 1, 2);
        assertEquals("NAME COLLISION", 0, bundle.getInt(PocketKnife_Bundles.ARG_BUNDLE, 0));
        assertEquals("NAME COLLISION", 1, bundle.getInt(PocketKnife_Bundles.ARG_BUNDLE1, 0));
        assertEquals("NAME COLLISION", 2, bundle.getInt(PocketKnife_Bundles.ARG_BUNDLE2, 0));

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

        assertEquals(aBoolean, bundle.getBoolean(PocketKnife_Bundles.ARG_A_BOOLEAN, false));
        assertArrayEquals(booleans, bundle.getBooleanArray(PocketKnife_Bundles.ARG_BOOLEANS));
        assertBundleEquals(bundle1, bundle.getBundle(PocketKnife_Bundles.ARG_BUNDLE));
        assertEquals(aByte, bundle.getByte(PocketKnife_Bundles.ARG_A_BYTE));
        assertArrayEquals(bytes, bundle.getByteArray(PocketKnife_Bundles.ARG_BYTES));
        assertEquals(aChar, bundle.getChar(PocketKnife_Bundles.ARG_A_CHAR, '0'));
        assertArrayEquals(chars, bundle.getCharArray(PocketKnife_Bundles.ARG_CHARS));
        assertEquals(charSequence, bundle.getCharSequence(PocketKnife_Bundles.ARG_CHAR_SEQUENCE));
        assertArrayEquals(charSequences, bundle.getCharSequenceArray(PocketKnife_Bundles.ARG_CHAR_SEQUENCES));
        assertArrayListEquals(charSequenceArrayList, bundle.getCharSequenceArrayList(PocketKnife_Bundles.ARG_CHAR_SEQUENCE_ARRAY_LIST));
        assertEquals(aDouble, bundle.getDouble(PocketKnife_Bundles.ARG_A_DOUBLE, 0.0), 0);
        assertArrayEquals(doubles, bundle.getDoubleArray(PocketKnife_Bundles.ARG_DOUBLES), 0);
        assertEquals(aFloat, bundle.getFloat(PocketKnife_Bundles.ARG_A_FLOAT, 0.0f), 0);
        assertArrayEquals(floats, bundle.getFloatArray(PocketKnife_Bundles.ARG_FLOATS), 0);
        assertEquals(anInt, bundle.getInt(PocketKnife_Bundles.ARG_AN_INT, 0));
        assertArrayEquals(ints, bundle.getIntArray(PocketKnife_Bundles.ARG_INTS));
        assertArrayListEquals(integerArrayList, bundle.getIntegerArrayList(PocketKnife_Bundles.ARG_INTEGER_ARRAY_LIST));
        assertEquals(aLong, bundle.getLong(PocketKnife_Bundles.ARG_A_LONG, 0));
        assertArrayEquals(longs, bundle.getLongArray(PocketKnife_Bundles.ARG_LONGS));
        assertEquals(parcelable, bundle.getParcelable(PocketKnife_Bundles.ARG_PARCELABLE));
        assertArrayEquals(parcelables, bundle.getParcelableArray(PocketKnife_Bundles.ARG_PARCELABLES));
        assertArrayListEquals(parcelableArrayList, bundle.getParcelableArrayList(PocketKnife_Bundles.ARG_PARCELABLE_ARRAY_LIST));
        assertEquals(serializable, bundle.getSerializable(PocketKnife_Bundles.ARG_SERIALIZABLE));
        assertEquals(aShort, bundle.getShort(PocketKnife_Bundles.ARG_A_SHORT, (short) 0));
        assertArrayEquals(shorts, bundle.getShortArray(PocketKnife_Bundles.ARG_SHORTS));
        assertEquals(string, bundle.getString(PocketKnife_Bundles.ARG_STRING));
        assertArrayEquals(strings, bundle.getStringArray(PocketKnife_Bundles.ARG_STRINGS));
        assertArrayListEquals(stringArrayList, bundle.getStringArrayList(PocketKnife_Bundles.ARG_STRING_ARRAY_LIST));
    }
}
