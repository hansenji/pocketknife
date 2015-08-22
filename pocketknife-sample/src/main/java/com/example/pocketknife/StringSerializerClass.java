package com.example.pocketknife;

import pocketknife.BindArgument;
import pocketknife.BindExtra;
import pocketknife.BundleSerializer;
import pocketknife.IntentSerializer;
import pocketknife.SaveState;

public class StringSerializerClass {
    @SaveState
    @BundleSerializer(StringSerializer.class)
    String saveString;

    @BindExtra
    @IntentSerializer(StringSerializer.class)
    String extraString;

    @BindArgument
    @BundleSerializer(StringSerializer.class)
    String argString;
}
