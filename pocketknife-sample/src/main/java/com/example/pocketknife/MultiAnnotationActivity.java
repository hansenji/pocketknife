package com.example.pocketknife;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import pocketknife.InjectExtra;
import pocketknife.PocketKnife;
import pocketknife.SaveState;

public class MultiAnnotationActivity extends FragmentActivity {
    public static final String EXTRA_INT = "EXTRA_INT";

    @InjectExtra(EXTRA_INT)
    @SaveState
    int i;

    MultiAnnotationFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.simple_activity);


        PocketKnife.injectExtras(this);
        PocketKnife.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PocketKnife.saveInstanceState(this, outState);
    }

    public void replaceFragment(MultiAnnotationFragment fragment) {
        this.fragment = fragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }
}
