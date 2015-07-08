package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import pocketknife.BindExtra;
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
    @BindExtra("Key")
    String s;
    // boolean dv
    @BindExtra(BOOLEAN_EXTRA)
    boolean aBoolean;
    // boolean[]
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required boolean[]
    @BindExtra(BOOLEAN_ARRAY_EXTRA)
    boolean[] booleans;
    // Bundle
    @BindExtra(BUNDLE_EXTRA)
    Bundle bundle;
    // byte dv
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required byte
    @BindExtra(BYTE_EXTRA)
    byte aByte;
    // byte[]
    @BindExtra(BYTE_ARRAY_EXTRA)
    byte[] bytes;
    // char dv
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required char
    @BindExtra(CHAR_EXTRA)
    char aChar;
    // char[]
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required char[]
    @BindExtra(CHAR_ARRAY_EXTRA)
    char[] chars;
    // CharSequence
    @BindExtra(CHAR_SEQUENCE_EXTRA)
    CharSequence charSequence;
    // CharSequence[]
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required CharSequence[]
    @BindExtra(CHAR_SEQUENCE_ARRAY_EXTRA)
    CharSequence[] charSequences;
    // ArrayList<CharSequence>
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required CharSequenceArrayList
    @BindExtra(CHAR_SEQUENCE_ARRAY_LIST_EXTRA)
    ArrayList<CharSequence> charSequenceArrayList;
    // double dv
    @BindExtra(DOUBLE_EXTRA)
    double aDouble;
    // double[]
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required double[]
    @BindExtra(DOUBLE_ARRAY_EXTRA)
    double[] doubles;
    // float dv
    @BindExtra(FLOAT_EXTRA)
    float aFloat;
    // float[]
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required float[]
    @BindExtra(FLOAT_ARRAY_EXTRA)
    float[] floats;
    // int dv
    @BindExtra(INT_EXTRA)
    int anInt;
    // int[]
    @BindExtra(INT_ARRAY_EXTRA)
    int[] ints;
    // ArrayList<Integer>
    @BindExtra(INTEGER_ARRAY_LIST_EXTRA)
    ArrayList<Integer> integerArrayList;
    // long dv
    @BindExtra(LONG_EXTRA)
    long aLong;
    // long[]
    @BindExtra(LONG_ARRAY_EXTRA)
    long[] longs;
    // Parcelable
    @BindExtra(PARCELABLE_EXTRA)
    MyParcelable parcelable;
    // Parcelable[]
    @BindExtra(PARCELABLE_ARRAY_EXTRA)
    MyParcelable[] parcelables;
    // ArrayList<Parcelable>
    @BindExtra(PARCELABLE_ARRAY_LIST_EXTRA)
    ArrayList<MyParcelable> parcelableArrayList;
    // Serializable
    @BindExtra(SERIALIZABLE_EXTRA)
    Serializable serializable;
    // short dv
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required Short
    @BindExtra(SHORT_EXTRA)
    short aShort;
    // short[]
    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required Short[]
    @BindExtra(SHORT_ARRAY_EXTRA)
    short[] shorts;
    // String
    @BindExtra(STRING_EXTRA)
    String string;
    // String[]
    @BindExtra(STRING_ARRAY_EXTRA)
    String[] strings;
    // ArrayList<String>
    @BindExtra(STRING_ARRAY_LIST_EXTRA)
    ArrayList<String> stringArrayList;
    // Not required
    @NotRequired
    @BindExtra(NOT_REQUIRED_INT_EXTRA)
    int notRequired = NRI_DEFAULT;
    @BindExtra(Intent.EXTRA_TEXT)
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_activity);

        PocketKnife.bindExtras(this);
    }

}
