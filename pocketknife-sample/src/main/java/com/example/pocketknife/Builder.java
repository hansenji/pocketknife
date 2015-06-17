package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import pocketknife.BundleBuilder;
import pocketknife.FragmentBuilder;
import pocketknife.IntentBuilder;
import pocketknife.Key;

public interface Builder {

    @BundleBuilder
    Bundle getBundle(int arg, @Key("THIS IS A TEST")int test);

    @IntentBuilder(action = Intent.ACTION_DEFAULT)
    Intent getIntent(int extra, @Key(Intent.EXTRA_TEXT) String text);

    @FragmentBuilder
    ParentFragment getFragment(int parentArg);
}
