package com.example.pocketknife;

import pocketknife.BindArgument;
import pocketknife.SaveState;

public class ChildFragment extends ParentFragment {

    public static final String CHILD_ARG = "CHILD_ARG";

    @BindArgument(CHILD_ARG)
    int childArg;

    @SaveState
    int cfInt;

}
