package com.example.pocketknife;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import pocketknife.InjectArgument;
import pocketknife.NotRequired;
import pocketknife.PocketKnife;
import pocketknife.SaveState;

import java.util.ArrayList;

public class SimpleFragment extends Fragment {

    private static final String STRING_ARG_KEY = "STRING_ARG_KEY";

    @InjectArgument(STRING_ARG_KEY)
    String stringArg;

    @SaveState
    protected int counter = 0;
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
    @NotRequired(Build.VERSION_CODES.HONEYCOMB_MR1)
    @SaveState
    String message = "Default Value";
    @SaveState
    MyOtherObj myOtherObj;

    public static Fragment newInstance(String stringArg) {
        Fragment fragment = new SimpleFragment();
        Bundle args = new Bundle();
        args.putString(STRING_ARG_KEY, stringArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PocketKnife.injectArguments(this);

        PocketKnife.restoreInstanceState(this, savedInstanceState);

        TextView textView = (TextView) getView().findViewById(R.id.textview);
        textView.setText(getString(R.string.count, counter));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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

    public static class MyOtherObj extends MyObj {

    }
}
