package com.example.pocketknife;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import pocketknife.Data;
import pocketknife.IntentBuilder;

import java.io.Serializable;
import java.util.ArrayList;

public interface Intents {

    @IntentBuilder(action = "TEST")
    Intent getActionIntent();

    @IntentBuilder(cls = SimpleActivity.class)
    Intent getClassIntent();

    @IntentBuilder(action = "TEST", flags = Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_NEW_TASK)
    Intent getFlagIntent();

    @IntentBuilder(action = "TEST")
    Intent getDataIntent(@Data String data);

    @IntentBuilder(action = "TEST")
    Intent getDataUriIntent(@Data Uri data);

    @IntentBuilder(action = "TEST", type = "application/html")
    Intent getTypeIntent();

    @IntentBuilder(action = "TEST", categories = "ONE")
    Intent getSingleCategory();

    @IntentBuilder(action = "TEST", categories = {"ONE", "TWO", "THREE", "FOUR" })
    Intent getCategories();

    @IntentBuilder(action = "TEST",
            categories = {"ONE", "TWO", "THREE", "FOUR" },
            cls = SimpleActivity.class,
            flags = Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_NEW_TASK,
            type = "application/html")
    Intent getAllPlusExtra(@Data String data, int i);

    @IntentBuilder(action = "TEST",
            categories = {"ONE", "TWO", "THREE", "FOUR" },
            cls = SimpleActivity.class,
            flags = Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_NEW_TASK,
            type = "application/html")
    Intent getAllDataUriPlusExtra(@Data Uri data, int i);

    @IntentBuilder(action = "TEST")
    Intent getExtra(int i);

    @IntentBuilder(action = "TEST")
    Intent getExtras(int i, int j);

    @IntentBuilder(action = "TEST")
    Intent getNameCollisionExtra(int intent);

    @IntentBuilder(action = "TEST")
    Intent getExtras(boolean aBoolean, boolean[] booleans, Bundle bundle, byte aByte, byte[] bytes, char aChar, char[] chars, CharSequence charSequence,
                     CharSequence[] charSequences, ArrayList<CharSequence> charSequenceArrayList, double aDouble, double[] doubles, float aFloat,
                     float[] floats, int anInt, int[] ints, ArrayList<Integer> integerArrayList, long aLong, long[] longs, MyParcelable parcelable,
                     MyParcelable[] parcelables, ArrayList<MyParcelable> parcelableArrayList, Serializable serializable, short aShort, short[] shorts,
                     String string, String[] strings, ArrayList<String> stringArrayList);
}