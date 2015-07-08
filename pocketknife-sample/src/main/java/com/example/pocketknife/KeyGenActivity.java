package com.example.pocketknife;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import pocketknife.BindExtra;
import pocketknife.PocketKnife;

public class KeyGenActivity extends FragmentActivity {

    @BindExtra
    String string;
    @BindExtra
    int blah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_activity);

        PocketKnife.bindExtras(this);
    }
}
