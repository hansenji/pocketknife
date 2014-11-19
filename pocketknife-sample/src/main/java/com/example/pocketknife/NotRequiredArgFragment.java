package com.example.pocketknife;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import pocketknife.InjectArgument;
import pocketknife.NotRequired;
import pocketknife.PocketKnife;

public class NotRequiredArgFragment extends Fragment {

    @InjectArgument("KEY")
    @NotRequired
    int i = 1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PocketKnife.injectArguments(this);
    }
}
