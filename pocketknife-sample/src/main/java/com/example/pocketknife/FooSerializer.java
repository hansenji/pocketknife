package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import pocketknife.PocketKnifeBundleSerializer;
import pocketknife.PocketKnifeIntentSerializer;

public class FooSerializer implements PocketKnifeBundleSerializer<Foo>, PocketKnifeIntentSerializer<Foo> {
    private static final String BAR = ".BAR";
    private static final String BAZ = ".BAZ";

    @Override
    public void put(Bundle bundle, Foo foo, String keyPrefix) {
        bundle.putString(keyPrefix + BAR, foo.getBar());
        bundle.putInt(keyPrefix + BAZ, foo.getBaz());
    }

    @Override
    public Foo get(Bundle bundle, Foo foo, String keyPrefix) {
        foo.setBar(bundle.getString(keyPrefix + BAR));
        foo.setBaz(bundle.getInt(keyPrefix + BAZ));
        return foo;
    }

    @Override
    public void put(Intent intent, Foo foo, String keyPrefix) {
        intent.putExtra(keyPrefix + BAR, foo.getBar());
        intent.putExtra(keyPrefix + BAZ, foo.getBaz());
    }

    @Override
    public Foo get(Intent intent, Foo foo, String keyPrefix) {
        foo.setBar(intent.getStringExtra(keyPrefix + BAR));
        foo.setBaz(intent.getIntExtra(keyPrefix + BAZ, 0));
        return foo;
    }
}
