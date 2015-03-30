package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml")
public class IntentBuilderTest extends BaseTest{

    private Intents intents;

    @Before
    public void setup() {
        intents = new PocketKnife_Intents(Robolectric.application);
    }

    @After
    public void tearDown() {
        intents = null;
    }

    @Test
    public void testAction() throws Exception {
        Intent intent = intents.getActionIntent();
        assertEquals("Action", "TEST", intent.getAction());
        intent = intents.getClassIntent();
        assertNull("No Action", intent.getAction());
        intent = intents.getAllPlusExtra(0);
        assertEquals("All", "TEST", intent.getAction());
    }

    @Test
    public void testClass() throws Exception {
        Intent intent = intents.getClassIntent();
        assertEquals("Class", SimpleActivity.class.getName(), intent.getComponent().getClassName());
        intent = intents.getActionIntent();
        assertNull("No Class", intent.getComponent());
        intent = intents.getAllPlusExtra(0);
        assertEquals("All", SimpleActivity.class.getName(), intent.getComponent().getClassName());
    }

    @Test
    public void testFlag() throws Exception {
        Intent intent = intents.getFlagIntent();
        assertEquals("Flags", Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_NEW_TASK, intent.getFlags());
        intent = intents.getActionIntent();
        assertEquals("No Flags", 0, intent.getFlags());
        intent = intents.getAllPlusExtra(0);
        assertEquals("All", Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_NEW_TASK, intent.getFlags());
    }

    @Test
    public void testData() throws Exception {
        Intent intent = intents.getDataIntent();
        assertEquals("Data", "data", intent.getDataString());
        intent = intents.getActionIntent();
        assertNull("No Data", intent.getData());
        intent = intents.getAllPlusExtra(0);
        assertEquals("All", "data", intent.getDataString());
    }

    @Test
    public void testCategories() throws Exception {
        Intent intent = intents.getActionIntent();
        Set<String> categories = intent.getCategories();
        assertEquals(0, categories == null ? 0 : categories.size()); // Have to test this way since robolectric doesn't return null for categories.
        intent = intents.getSingleCategory();
        categories = intent.getCategories();
        assertEquals(1, categories.size());
        assertTrue(categories.containsAll(Collections.singletonList("ONE")));
        intent = intents.getCategories();
        categories = intent.getCategories();
        assertEquals(4, categories.size());
        assertTrue(categories.containsAll(Arrays.asList("ONE", "TWO", "THREE", "FOUR")));
        intent = intents.getAllPlusExtra(0);
        categories = intent.getCategories();
        assertEquals(4, categories.size());
        assertTrue(categories.containsAll(Arrays.asList("ONE", "TWO", "THREE", "FOUR")));
    }

    @Test
    public void testType() throws Exception {
        Intent intent = intents.getTypeIntent();
        assertEquals("Type", "application/html", intent.getType());
        intent = intents.getActionIntent();
        assertNull("No Type", intent.getType());
        intent = intents.getAllPlusExtra(0);
        assertEquals("All", "application/html", intent.getType());
    }

    @Test
    public void testExtras() throws Exception {
        Random random = new Random(42);
        int i = random.nextInt();
        int j = random.nextInt();
        Intent intent = intents.getExtra(i);
        assertEquals("EXTRA", i, intent.getIntExtra(PocketKnife_Intents.EXTRA_I, 0));
        intent = intents.getActionIntent();
        assertNull("NO EXTRAS", intent.getExtras());

        intent = intents.getExtras(i, j);
        assertEquals("SAME TYPE I", i, intent.getIntExtra(PocketKnife_Intents.EXTRA_I, 0));
        assertEquals("SAME TYPE J", j, intent.getIntExtra(PocketKnife_Intents.EXTRA_J, 0));

        intent = intents.getNameCollisionExtra(i);
        assertEquals("NAME COLLISION", i, intent.getIntExtra(PocketKnife_Intents.EXTRA_INTENT, 0));

        boolean aBoolean = random.nextBoolean();
        boolean[] booleans = {random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean()};
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_INT_ARG, random.nextInt());
        bundle.putSerializable(BUNDLE_SERIALIZABLE_ARG, UUID.randomUUID().toString());
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
        intent = intents.getExtras(aBoolean, booleans, bundle, aByte, bytes, aChar, chars, charSequence, charSequences, charSequenceArrayList, aDouble, doubles,
                aFloat, floats, anInt, ints, integerArrayList, aLong, longs, parcelable, parcelables, parcelableArrayList, serializable, aShort,
                shorts, string, strings, stringArrayList);

        assertEquals(aBoolean, intent.getBooleanExtra(PocketKnife_Intents.EXTRA_A_BOOLEAN, false));
        assertArrayEquals(booleans, intent.getBooleanArrayExtra(PocketKnife_Intents.EXTRA_BOOLEANS));
        assertBundleEquals(bundle, intent.getBundleExtra(PocketKnife_Intents.EXTRA_BUNDLE));
        assertEquals(aByte, intent.getByteExtra(PocketKnife_Intents.EXTRA_A_BYTE, (byte) 0));
        assertArrayEquals(bytes, intent.getByteArrayExtra(PocketKnife_Intents.EXTRA_BYTES));
        assertEquals(aChar, intent.getCharExtra(PocketKnife_Intents.EXTRA_A_CHAR, '0'));
        assertArrayEquals(chars, intent.getCharArrayExtra(PocketKnife_Intents.EXTRA_CHARS));
        assertEquals(charSequence, intent.getCharSequenceExtra(PocketKnife_Intents.EXTRA_CHAR_SEQUENCE));
        assertArrayEquals(charSequences, intent.getCharSequenceArrayExtra(PocketKnife_Intents.EXTRA_CHAR_SEQUENCES));
        assertArrayListEquals(charSequenceArrayList, intent.getCharSequenceArrayListExtra(PocketKnife_Intents.EXTRA_CHAR_SEQUENCE_ARRAY_LIST));
        assertEquals(aDouble, intent.getDoubleExtra(PocketKnife_Intents.EXTRA_A_DOUBLE, 0.0), 0);
        assertArrayEquals(doubles, intent.getDoubleArrayExtra(PocketKnife_Intents.EXTRA_DOUBLES), 0);
        assertEquals(aFloat, intent.getFloatExtra(PocketKnife_Intents.EXTRA_A_FLOAT, 0.0f), 0);
        assertArrayEquals(floats, intent.getFloatArrayExtra(PocketKnife_Intents.EXTRA_FLOATS), 0);
        assertEquals(anInt, intent.getIntExtra(PocketKnife_Intents.EXTRA_AN_INT, 0));
        assertArrayEquals(ints, intent.getIntArrayExtra(PocketKnife_Intents.EXTRA_INTS));
        assertArrayListEquals(integerArrayList, intent.getIntegerArrayListExtra(PocketKnife_Intents.EXTRA_INTEGER_ARRAY_LIST));
        assertEquals(aLong, intent.getLongExtra(PocketKnife_Intents.EXTRA_A_LONG, 0));
        assertArrayEquals(longs, intent.getLongArrayExtra(PocketKnife_Intents.EXTRA_LONGS));
        assertEquals(parcelable, intent.getParcelableExtra(PocketKnife_Intents.EXTRA_PARCELABLE));
        assertArrayEquals(parcelables, intent.getParcelableArrayExtra(PocketKnife_Intents.EXTRA_PARCELABLES));
        assertArrayListEquals(parcelableArrayList, intent.getParcelableArrayListExtra(PocketKnife_Intents.EXTRA_PARCELABLE_ARRAY_LIST));
        assertEquals(serializable, intent.getSerializableExtra(PocketKnife_Intents.EXTRA_SERIALIZABLE));
        assertEquals(aShort, intent.getShortExtra(PocketKnife_Intents.EXTRA_A_SHORT, (short) 0));
        assertArrayEquals(shorts, intent.getShortArrayExtra(PocketKnife_Intents.EXTRA_SHORTS));
        assertEquals(string, intent.getStringExtra(PocketKnife_Intents.EXTRA_STRING));
        assertArrayEquals(strings, intent.getStringArrayExtra(PocketKnife_Intents.EXTRA_STRINGS));
        assertArrayListEquals(stringArrayList, intent.getStringArrayListExtra(PocketKnife_Intents.EXTRA_STRING_ARRAY_LIST));
    }

}