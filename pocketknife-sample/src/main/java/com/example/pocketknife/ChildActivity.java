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

    GrandchildFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            fragment = GrandchildFragment.newInstance();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        } else {
            fragment = (GrandchildFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        }
    }
}
