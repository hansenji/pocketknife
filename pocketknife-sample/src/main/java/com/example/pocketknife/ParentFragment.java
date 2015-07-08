package com.example.pocketknife;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pocketknife.BindArgument;
import pocketknife.PocketKnife;
import pocketknife.SaveState;

public class ParentFragment extends Fragment {

    public static final String PARENT_ARG = "PARENT_ARG";

    @BindArgument(PARENT_ARG)
    int parentArg;

    @SaveState
    int pfInt;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PocketKnife.bindArguments(this);
        PocketKnife.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PocketKnife.saveInstanceState(this, outState);
    }
}
