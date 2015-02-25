package com.example.pocketknife;

import pocketknife.InjectArgument;
import pocketknife.SaveState;

public class ChildFragment extends ParentFragment {

    public static final String CHILD_ARG = "CHILD_ARG";

    @InjectArgument(CHILD_ARG)
    int childArg;

    @SaveState
    int cfInt;

    public static ChildFragment newInstance() {
        return new ChildFragment();
    }
}
