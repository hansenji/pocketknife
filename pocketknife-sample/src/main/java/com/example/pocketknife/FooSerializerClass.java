package com.example.pocketknife;

import pocketknife.BindArgument;
import pocketknife.BindExtra;
import pocketknife.BundleSerializer;
import pocketknife.IntentSerializer;
import pocketknife.SaveState;

public class FooSerializerClass {
    @SaveState
    @BundleSerializer(FooSerializer.class)
    Foo saveFoo;

    @BindArgument
    @BundleSerializer(FooSerializer.class)
    Foo argFoo;

    @BindExtra
    @IntentSerializer(FooSerializer.class)
    Foo extraFoo;
}
