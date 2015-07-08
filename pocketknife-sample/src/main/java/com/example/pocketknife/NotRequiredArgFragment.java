package com.example.pocketknife;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import pocketknife.BindArgument;
import pocketknife.NotRequired;
import pocketknife.PocketKnife;

public class NotRequiredArgFragment extends Fragment {

    @BindArgument("KEY")
    @NotRequired
    int i = 1;

    @BindArgument("STRING")
    @NotRequired
    String s = "NOT_REQUIRED";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PocketKnife.bindArguments(this);
    }
}
