package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import pocketknife.BundleBuilder;
import pocketknife.FragmentBuilder;
import pocketknife.IntentBuilder;
import pocketknife.Key;

public interface Builder {

    @BundleBuilder
    Bundle getBundle(int arg, @Key("THIS IS A TEST")int test);

    @BundleBuilder
    Bundle getBundle(@Key("Testing is awesome") int arg);

    @IntentBuilder(action = Intent.ACTION_DEFAULT)
    Intent getIntent(int extra, @Key(Intent.EXTRA_TEXT) String text);

    @IntentBuilder(action = Intent.ACTION_DEFAULT)
    Intent getIntent(String text);

    @FragmentBuilder
    Fragment getFragment(int arg);

    @FragmentBuilder
    Fragment getFragment(@Key("Fragment Test") int arg, int arg2);

}
