package com.example.pocketknife;

import android.os.Bundle;
import pocketknife.BundleBuilder;

import java.io.Serializable;
import java.util.ArrayList;

public interface Bundles {

    @BundleBuilder
    Bundle getEmptyBundle();

    @BundleBuilder
    Bundle getSingleArgBundle(int i);

    @BundleBuilder
    Bundle getTwinArgsBundle(int i, int j);

    @BundleBuilder
    Bundle getNameCollisionBundle(int bundle, int bundle1, int bundle2);

    @BundleBuilder
    Bundle getBundle(boolean aBoolean, boolean[] booleans, Bundle bundle, byte aByte, byte[] bytes, char aChar, char[] chars, CharSequence charSequence,
                     CharSequence[] charSequences, ArrayList<CharSequence> charSequenceArrayList, double aDouble, double[] doubles, float aFloat,
                     float[] floats, int anInt, int[] ints, ArrayList<Integer> integerArrayList, long aLong, long[] longs, MyParcelable parcelable,
                     MyParcelable[] parcelables, ArrayList<MyParcelable> parcelableArrayList, Serializable serializable, short aShort, short[] shorts,
                     String string, String[] strings, ArrayList<String> stringArrayList);

}
