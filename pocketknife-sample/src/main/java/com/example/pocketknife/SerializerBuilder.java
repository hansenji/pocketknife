package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import pocketknife.BundleBuilder;
import pocketknife.BundleSerializer;
import pocketknife.FragmentBuilder;
import pocketknife.IntentBuilder;
import pocketknife.IntentSerializer;

public interface SerializerBuilder {

    @BundleBuilder
    Bundle buildBundle(@BundleSerializer(StringSerializer.class) String argString);

    @IntentBuilder(action = "test")
    Intent buildIntent(@IntentSerializer(StringSerializer.class) String extraString);

    @FragmentBuilder
    Fragment buildFragment(@BundleSerializer(StringSerializer.class) String argString);

    @BundleBuilder
    Bundle buildBundle(@BundleSerializer(FooSerializer.class) Foo argFoo);

    @IntentBuilder(action = "test")
    Intent buildIntent(@IntentSerializer(FooSerializer.class)Foo extraFoo);

    @FragmentBuilder
    Fragment buildFragment(@BundleSerializer(FooSerializer.class) Foo argFoo);
}
