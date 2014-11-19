package com.example.pocketknife;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import pocketknife.PocketKnife;
import pocketknife.SaveState;

import java.io.Serializable;
import java.util.ArrayList;

public class SaveStateActivity extends FragmentActivity {

    // boolean dv
    @SaveState
    boolean aBoolean;
    // boolean[]
    @SaveState
    boolean[] booleans;
    // Bundle
    @SaveState
    Bundle bundle;
    // byte dv
    @SaveState
    byte aByte;
    // byte[]
    @SaveState
    byte[] bytes;
    // char dv
    @SaveState
    char aChar;
    // char[]
    @SaveState
    char[] chars;
    // CharSequence
    @SaveState
    CharSequence charSequence;
    // CharSequence[]
    @SaveState
    CharSequence[] charSequences;
    // ArrayList<CharSequence>
    @SaveState
    ArrayList<CharSequence> charSequenceArrayList;
    // double dv
    @SaveState
    double aDouble;
    // double[]
    @SaveState
    double[] doubles;
    // float dv
    @SaveState
    float aFloat;
    // float[]
    @SaveState
    float[] floats;
    // int dv
    @SaveState
    int anInt;
    // int[]
    @SaveState
    int[] ints;
    // ArrayList<Integer>
    @SaveState
    ArrayList<Integer> integerArrayList;
    // long dv
    @SaveState
    long aLong;
    // long[]
    @SaveState
    long[] longs;
    // Parcelable
    @SaveState
    MyParcelable parcelable;
    // Parcelable[]
    @SaveState
    MyParcelable[] parcelables;
    // ArrayList<Parcelable>
    @SaveState
    ArrayList<MyParcelable> parcelableArrayList;
    // Serializable
    @SaveState
    Serializable serializable;
    // short dv
    @SaveState
    short aShort;
    // short[]
    @SaveState
    short[] shorts;
    // String
    @SaveState
    String string;
    // String[]
    @SaveState
    String[] strings;
    // ArrayList<String>
    @SaveState
    ArrayList<String> stringArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_activity);

        PocketKnife.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PocketKnife.saveInstanceState(this, outState);
    }
}
