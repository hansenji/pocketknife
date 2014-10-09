package com.example.pocketknife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.widget.TextView;
import pocketknife.PocketKnife;
import pocketknife.SaveState;

import java.util.ArrayList;


public class SimpleActivity extends Activity {

    @SaveState(defaultValue="0")
    int counter = 0;
    @SaveState
    int[] array = null;
    @SaveState
    long[][] longArray = null;
    @SaveState
    Intent intent = null;
    @SaveState
    SparseArray<Parcelable> parcelableSparseArray = null;
    @SaveState
    SparseArray<MyObj> myObjSparseArray = null;
    @SaveState
    ArrayList<Integer> integerArrayList = null;
    @SaveState(defaultValue = "\"HelloWorld\"", minSdk = 12)
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_activity);

        PocketKnife.restoreInstanceState(this, savedInstanceState);

        TextView textView = (TextView) findViewById(R.id.textview);
        textView.setText(getString(R.string.count, counter));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        counter++;
        PocketKnife.saveInstanceState(this, outState);
    }

    public static class MyObj implements Parcelable {

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {

        }
    }

}
