package com.example.pocketknife;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pocketknife.InjectArgument;
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
    @InjectArgument("Key")
    String s;
    // boolean dv
    @InjectArgument(BOOLEAN)
    boolean aBoolean;
    // boolean[]
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required boolean[]
    @InjectArgument(BOOLEAN_ARRAY)
    boolean[] booleans;
    // Bundle
    @InjectArgument(BUNDLE)
    Bundle bundle;
    // byte dv
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required byte
    @InjectArgument(BYTE)
    byte aByte;
    // byte[]
    @InjectArgument(BYTE_ARRAY)
    byte[] bytes;
    // char dv
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required char
    @InjectArgument(CHAR)
    char aChar;
    // char[]
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required char[]
    @InjectArgument(CHAR_ARRAY)
    char[] chars;
    // CharSequence
    @InjectArgument(CHAR_SEQUENCE)
    CharSequence charSequence;
    // CharSequence[]
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required CharSequence[]
    @InjectArgument(CHAR_SEQUENCE_ARRAY)
    CharSequence[] charSequences;
    // ArrayList<CharSequence>
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required CharSequenceArrayList
    @InjectArgument(CHAR_SEQUENCE_ARRAY_LIST)
    ArrayList<CharSequence> charSequenceArrayList;
    // double dv
    @InjectArgument(DOUBLE)
    double aDouble;
    // double[]
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required double[]
    @InjectArgument(DOUBLE_ARRAY)
    double[] doubles;
    // float dv
    @InjectArgument(FLOAT)
    float aFloat;
    // float[]
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required float[]
    @InjectArgument(FLOAT_ARRAY)
    float[] floats;
    // int dv
    @InjectArgument(INT)
    int anInt;
    // int[]
    @InjectArgument(INT_ARRAY)
    int[] ints;
    // ArrayList<Integer>
    @InjectArgument(INTEGER_ARRAY_LIST)
    ArrayList<Integer> integerArrayList;
    // long dv
    @InjectArgument(LONG)
    long aLong;
    // long[]
    @InjectArgument(LONG_ARRAY)
    long[] longs;
    // Parcelable
    @InjectArgument(PARCELABLE)
    MyParcelable parcelable;
    // Parcelable[]
    @InjectArgument(PARCELABLE_ARRAY)
    MyParcelable[] parcelables;
    // ArrayList<Parcelable>
    @InjectArgument(PARCELABLE_ARRAY_LIST)
    ArrayList<MyParcelable> parcelableArrayList;
    // Serializable
    @InjectArgument(SERIALIZABLE)
    Serializable serializable;
    // short dv
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required Short
    @InjectArgument(SHORT)
    short aShort;
    // short[]
//    @NotRequired // ROBOLECTRIC implementation of intent doesn't allow for Required Short[]
    @InjectArgument(SHORT_ARRAY)
    short[] shorts;
    // String
    @InjectArgument(STRING)
    String string;
    // String[]
    @InjectArgument(STRING_ARRAY)
    String[] strings;
    // ArrayList<String>
    @InjectArgument(STRING_ARRAY_LIST)
    ArrayList<String> stringArrayList;
    // Not required
    @NotRequired
    @InjectArgument(NOT_REQUIRED_INT)
    int notRequired = NRI_DEFAULT;


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
        PocketKnife.injectArguments(this, getArguments());
    }
}
