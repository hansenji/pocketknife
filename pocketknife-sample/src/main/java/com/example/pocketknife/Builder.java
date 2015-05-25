package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import pocketknife.BundleBuilder;
import pocketknife.IntentBuilder;

public interface Builder {

    @BundleBuilder
    Bundle getBundle(int arg);

    @IntentBuilder(action = Intent.ACTION_DEFAULT)
    Intent getIntent(int extra);
}
