package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import pocketknife.InjectExtra;
import pocketknife.NotRequired;
import pocketknife.PocketKnife;

import java.io.Serializable;
import java.util.ArrayList;

public class SimpleActivity extends FragmentActivity {

    public static final String BOOLEAN_EXTRA = "BOOLEAN_EXTRA";
    public static final String BOOLEAN_ARRAY_EXTRA = "BOOLEAN_ARRAY_EXTRA";
    public static final String BUNDLE_EXTRA = "BUNDLE_EXTRA";
    public static final String BYTE_EXTRA = "BYTE_EXTRA";
    public static final String BYTE_ARRAY_EXTRA = "BYTE_ARRAY_EXTRA";
    public static final String CHAR_EXTRA = "CHAR_EXTRA";
    public static final String CHAR_ARRAY_EXTRA = "CHAR_ARRAY_EXTRA";
    public static final String CHAR_SEQUENCE_EXTRA = "CHAR_SEQUENCE_EXTRA";
    public static final String CHAR_SEQUENCE_ARRAY_EXTRA = "CHAR_SEQUENCE_ARRAY_EXTRA";
    public static final String CHAR_SEQUENCE_ARRAY_LIST_EXTRA = "CHAR_SEQUENCE_ARRAY_LIST_EXTRA";
    public static final String DOUBLE_EXTRA = "DOUBLE_EXTRA";
    public static final String DOUBLE_ARRAY_EXTRA = "DOUBLE_ARRAY_EXTRA";
    public static final String FLOAT_EXTRA = "FLOAT_EXTRA";
    public static final String FLOAT_ARRAY_EXTRA = "FLOAT_ARRAY_EXTRA";
    public static final String INT_EXTRA = "INT_EXTRA";
    public static final String INT_ARRAY_EXTRA = "INT_ARRAY_EXTRA";
    public static final String INTEGER_ARRAY_LIST_EXTRA = "INTEGER_ARRAY_LIST_EXTRA";
    public static final String LONG_EXTRA = "LONG_EXTRA";
    public static final String LONG_ARRAY_EXTRA = "LONG_ARRAY_EXTRA";
    public static final String PARCELABLE_EXTRA = "PARCELABLE_EXTRA";
    public static final String PARCELABLE_ARRAY_EXTRA = "PARCELABLE_ARRAY_EXTRA";
    public static final String PARCELABLE_ARRAY_LIST_EXTRA = "PARCELABLE_ARRAY_LIST_EXTRA";
    public static final String SERIALIZABLE_EXTRA = "SERIALIZABLE_EXTRA";
    public static final String SHORT_EXTRA = "SHORT_EXTRA";
    public static final String SHORT_ARRAY_EXTRA = "SHORT_ARRAY_EXTRA";
    public static final String STRING_EXTRA = "STRING_EXTRA";
    public static final String STRING_ARRAY_EXTRA = "STRING_ARRAY_EXTRA";
    public static final String STRING_ARRAY_LIST_EXTRA = "STRING_ARRAY_LIST_EXTRA";
    public static final String NOT_REQUIRED_INT_EXTRA = "NOT_REQUIRED_INT_EXTRA";
    public static final int NRI_DEFAULT = 101;

    @NotRequired
    @InjectExtra("Key")
    String s;
    // boolean dv
    @InjectExtra(BOOLEAN_EXTRA)
    boolean aBoolean;
    // boolean[]
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required boolean[]
    @InjectExtra(BOOLEAN_ARRAY_EXTRA)
    boolean[] booleans;
    // Bundle
    @InjectExtra(BUNDLE_EXTRA)
    Bundle bundle;
    // byte dv
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required byte
    @InjectExtra(BYTE_EXTRA)
    byte aByte;
    // byte[]
    @InjectExtra(BYTE_ARRAY_EXTRA)
    byte[] bytes;
    // char dv
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required char
    @InjectExtra(CHAR_EXTRA)
    char aChar;
    // char[]
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required char[]
    @InjectExtra(CHAR_ARRAY_EXTRA)
    char[] chars;
    // CharSequence
    @InjectExtra(CHAR_SEQUENCE_EXTRA)
    CharSequence charSequence;
    // CharSequence[]
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required CharSequence[]
    @InjectExtra(CHAR_SEQUENCE_ARRAY_EXTRA)
    CharSequence[] charSequences;
    // ArrayList<CharSequence>
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required CharSequenceArrayList
    @InjectExtra(CHAR_SEQUENCE_ARRAY_LIST_EXTRA)
    ArrayList<CharSequence> charSequenceArrayList;
    // double dv
    @InjectExtra(DOUBLE_EXTRA)
    double aDouble;
    // double[]
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required double[]
    @InjectExtra(DOUBLE_ARRAY_EXTRA)
    double[] doubles;
    // float dv
    @InjectExtra(FLOAT_EXTRA)
    float aFloat;
    // float[]
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required float[]
    @InjectExtra(FLOAT_ARRAY_EXTRA)
    float[] floats;
    // int dv
    @InjectExtra(INT_EXTRA)
    int anInt;
    // int[]
    @InjectExtra(INT_ARRAY_EXTRA)
    int[] ints;
    // ArrayList<Integer>
    @InjectExtra(INTEGER_ARRAY_LIST_EXTRA)
    ArrayList<Integer> integerArrayList;
    // long dv
    @InjectExtra(LONG_EXTRA)
    long aLong;
    // long[]
    @InjectExtra(LONG_ARRAY_EXTRA)
    long[] longs;
    // Parcelable
    @InjectExtra(PARCELABLE_EXTRA)
    MyParcelable parcelable;
    // Parcelable[]
    @InjectExtra(PARCELABLE_ARRAY_EXTRA)
    MyParcelable[] parcelables;
    // ArrayList<Parcelable>
    @InjectExtra(PARCELABLE_ARRAY_LIST_EXTRA)
    ArrayList<MyParcelable> parcelableArrayList;
    // Serializable
    @InjectExtra(SERIALIZABLE_EXTRA)
    Serializable serializable;
    // short dv
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required Short
    @InjectExtra(SHORT_EXTRA)
    short aShort;
    // short[]
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required Short[]
    @InjectExtra(SHORT_ARRAY_EXTRA)
    short[] shorts;
    // String
    @InjectExtra(STRING_EXTRA)
    String string;
    // String[]
    @InjectExtra(STRING_ARRAY_EXTRA)
    String[] strings;
    // ArrayList<String>
    @InjectExtra(STRING_ARRAY_LIST_EXTRA)
    ArrayList<String> stringArrayList;
    // Not required
    @NotRequired
    @InjectExtra(NOT_REQUIRED_INT_EXTRA)
    int notRequired = NRI_DEFAULT;
    @InjectExtra(Intent.EXTRA_TEXT)
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_activity);

        PocketKnife.injectExtras(this);
    }

}
