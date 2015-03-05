package com.example.pocketknife;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pocketknife.InjectArgument;
import pocketknife.PocketKnife;
import pocketknife.SaveState;

public class MultiAnnotationFragment extends Fragment {

    public static final String ARG_INT = "ARG_INT";

    @InjectArgument(ARG_INT)
    @SaveState
    int i;

    public static MultiAnnotationFragment newInstance() {
        return new MultiAnnotationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PocketKnife.injectArguments(this, getArguments());
        PocketKnife.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PocketKnife.saveInstanceState(this, outState);
    }
}
