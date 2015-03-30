package com.example.pocketknife;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import pocketknife.InjectExtra;
import pocketknife.PocketKnife;

public class KeyGenActivity extends FragmentActivity {

    @InjectExtra
    String string;
    @InjectExtra
    int blah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_activity);

        PocketKnife.injectExtras(this);
    }
}
