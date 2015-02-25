package com.example.pocketknife;

import android.os.Bundle;
import pocketknife.InjectExtra;
import pocketknife.SaveState;

public class ChildActivity extends ParentActivity {

    public static final String CHILD_EXTRA = "CHILD_EXTRA";
    public static final String FRAGMENT_ARGS = "FRAGMENT_ARGS";

    @InjectExtra(CHILD_EXTRA)
    int childExtra;

    @InjectExtra(FRAGMENT_ARGS)
    Bundle args;

    @SaveState
    int caInt;

    ChildFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            fragment = ChildFragment.newInstance();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        } else {
            fragment = (ChildFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        }
    }
}
