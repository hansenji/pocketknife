package com.example.pocketknife;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import pocketknife.InjectExtra;
import pocketknife.PocketKnife;


public class SimpleActivity extends FragmentActivity {

    @InjectExtra("Key")
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_activity);

        PocketKnife.injectExtras(this);

        if (savedInstanceState == null) {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            supportFragmentManager.beginTransaction().replace(R.id.container, SimpleFragment.newInstance("I AM AWESOME")).commit();
        }

    }

}
