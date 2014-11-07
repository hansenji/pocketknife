package com.example.pocketknife;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import pocketknife.PocketKnife;
import pocketknife.SaveState;

public class SimpleFragmentActivity extends FragmentActivity {

    @SaveState
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.simple_activity);

        PocketKnife.restoreInstanceState(this, savedInstanceState);

        if (savedInstanceState == null) {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            supportFragmentManager.beginTransaction().replace(R.id.container, SimpleFragment.newInstance("I AM AWESOME")).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PocketKnife.saveInstanceState(this, outState);
    }
}
