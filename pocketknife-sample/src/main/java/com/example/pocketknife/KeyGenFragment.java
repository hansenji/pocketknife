package com.example.pocketknife;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pocketknife.BindArgument;
import pocketknife.PocketKnife;

public class KeyGenFragment extends Fragment {

    @BindArgument
    String aString;
    @BindArgument
    int anInt;

    public static KeyGenFragment newInstance() {
        return new KeyGenFragment();
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
