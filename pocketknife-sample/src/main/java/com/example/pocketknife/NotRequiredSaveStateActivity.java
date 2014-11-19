package com.example.pocketknife;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import pocketknife.NotRequired;
import pocketknife.PocketKnife;
import pocketknife.SaveState;

public class NotRequiredSaveStateActivity extends FragmentActivity {

    @SaveState
    @NotRequired
    int i = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_activity);

        PocketKnife.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PocketKnife.saveInstanceState(this, outState);
    }
}
