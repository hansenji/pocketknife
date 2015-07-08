package com.example.pocketknife;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import pocketknife.BindExtra;
import pocketknife.NotRequired;
import pocketknife.PocketKnife;
import pocketknife.SaveState;

public class SimpleFragmentActivity extends FragmentActivity {

    @BindExtra("INT")
    @NotRequired
    int intExtra = 2;
    @BindExtra("STRING")
    @NotRequired
    String stringExtra = "NOT_REQUIRED";

    @SaveState
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.simple_activity);
        PocketKnife.bindExtras(this);
        PocketKnife.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PocketKnife.saveInstanceState(this, outState);
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }
}
