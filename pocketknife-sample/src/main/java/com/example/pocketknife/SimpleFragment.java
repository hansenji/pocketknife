package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pocketknife.BindArgument;
import pocketknife.NotRequired;
import pocketknife.PocketKnife;

import java.io.Serializable;
import java.util.ArrayList;

public class SimpleFragment extends Fragment {

    public static final String BOOLEAN = "BOOLEAN";
    public static final String BOOLEAN_ARRAY = "BOOLEAN_ARRAY";
    public static final String BUNDLE = "BUNDLE";
    public static final String BYTE = "BYTE";
    public static final String BYTE_ARRAY = "BYTE_ARRAY";
    public static final String CHAR = "CHAR";
    public static final String CHAR_ARRAY = "CHAR_ARRAY";
    public static final String CHAR_SEQUENCE = "CHAR_SEQUENCE";
    public static final String CHAR_SEQUENCE_ARRAY = "CHAR_SEQUENCE_ARRAY";
    public static final String CHAR_SEQUENCE_ARRAY_LIST = "CHAR_SEQUENCE_ARRAY_LIST";
    public static final String DOUBLE = "DOUBLE";
    public static final String DOUBLE_ARRAY = "DOUBLE_ARRAY";
    public static final String FLOAT = "FLOAT";
    public static final String FLOAT_ARRAY = "FLOAT_ARRAY";
    public static final String INT = "INT";
    public static final String INT_ARRAY = "INT_ARRAY";
    public static final String INTEGER_ARRAY_LIST = "INTEGER_ARRAY_LIST";
    public static final String LONG = "LONG";
    public static final String LONG_ARRAY = "LONG_ARRAY";
    public static final String PARCELABLE = "PARCELABLE";
    public static final String PARCELABLE_ARRAY = "PARCELABLE_ARRAY";
    public static final String PARCELABLE_ARRAY_LIST = "PARCELABLE_ARRAY_LIST";
    public static final String SERIALIZABLE = "SERIALIZABLE";
    public static final String SHORT = "SHORT";
    public static final String SHORT_ARRAY = "SHORT_ARRAY";
    public static final String STRING = "STRING";
    public static final String STRING_ARRAY = "STRING_ARRAY";
    public static final String STRING_ARRAY_LIST = "STRING_ARRAY_LIST";
    public static final String NOT_REQUIRED_INT = "NOT_REQUIRED_INT";
    public static final int NRI_DEFAULT = 101;

    @NotRequired
    @BindArgument("Key")
    String s;
    // boolean dv
    @BindArgument(BOOLEAN)
    boolean aBoolean;
    // boolean[]
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required boolean[]
    @BindArgument(BOOLEAN_ARRAY)
    boolean[] booleans;
    // Bundle
    @BindArgument(BUNDLE)
    Bundle bundle;
    // byte dv
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required byte
    @BindArgument(BYTE)
    byte aByte;
    // byte[]
    @BindArgument(BYTE_ARRAY)
    byte[] bytes;
    // char dv
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required char
    @BindArgument(CHAR)
    char aChar;
    // char[]
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required char[]
    @BindArgument(CHAR_ARRAY)
    char[] chars;
    // CharSequence
    @BindArgument(CHAR_SEQUENCE)
    CharSequence charSequence;
    // CharSequence[]
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required CharSequence[]
    @BindArgument(CHAR_SEQUENCE_ARRAY)
    CharSequence[] charSequences;
    // ArrayList<CharSequence>
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required CharSequenceArrayList
    @BindArgument(CHAR_SEQUENCE_ARRAY_LIST)
    ArrayList<CharSequence> charSequenceArrayList;
    // double dv
    @BindArgument(DOUBLE)
    double aDouble;
    // double[]
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required double[]
    @BindArgument(DOUBLE_ARRAY)
    double[] doubles;
    // float dv
    @BindArgument(FLOAT)
    float aFloat;
    // float[]
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required float[]
    @BindArgument(FLOAT_ARRAY)
    float[] floats;
    // int dv
    @BindArgument(INT)
    int anInt;
    // int[]
    @BindArgument(INT_ARRAY)
    int[] ints;
    // ArrayList<Integer>
    @BindArgument(INTEGER_ARRAY_LIST)
    ArrayList<Integer> integerArrayList;
    // long dv
    @BindArgument(LONG)
    long aLong;
    // long[]
    @BindArgument(LONG_ARRAY)
    long[] longs;
    // Parcelable
    @BindArgument(PARCELABLE)
    MyParcelable parcelable;
    // Parcelable[]
    @BindArgument(PARCELABLE_ARRAY)
    MyParcelable[] parcelables;
    // ArrayList<Parcelable>
    @BindArgument(PARCELABLE_ARRAY_LIST)
    ArrayList<MyParcelable> parcelableArrayList;
    // Serializable
    @BindArgument(SERIALIZABLE)
    Serializable serializable;
    // short dv
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required Short
    @BindArgument(SHORT)
    short aShort;
    // short[]
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required Short[]
    @BindArgument(SHORT_ARRAY)
    short[] shorts;
    // String
    @BindArgument(STRING)
    String string;
    // String[]
    @BindArgument(STRING_ARRAY)
    String[] strings;
    // ArrayList<String>
    @BindArgument(STRING_ARRAY_LIST)
    ArrayList<String> stringArrayList;
    // Not required
    @NotRequired
    @BindArgument(NOT_REQUIRED_INT)
    int notRequired = NRI_DEFAULT;
    @BindArgument(Intent.EXTRA_TEXT)
    String text;


    public static SimpleFragment newInstance() {
        return new SimpleFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PocketKnife.bindArguments(this, getArguments());
    }
}
