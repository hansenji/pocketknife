package com.example.pocketknife;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class SimpleFragmentActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.simple_activity);

        if (savedInstanceState == null) {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            supportFragmentManager.beginTransaction().replace(R.id.container, SimpleFragment.newInstance("I AM AWESOME")).commit();
        }
    }
}
