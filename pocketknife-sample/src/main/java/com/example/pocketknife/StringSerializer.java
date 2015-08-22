package com.example.pocketknife;

import android.content.Intent;
import android.os.Bundle;
import pocketknife.PocketKnifeBundleSerializer;
import pocketknife.PocketKnifeIntentSerializer;

public class StringSerializer implements PocketKnifeBundleSerializer<String>, PocketKnifeIntentSerializer<String> {

    @Override
    public void put(Bundle bundle, String target, String keyPrefix) {
        char[] charArray = (keyPrefix + target).toCharArray();
        bundle.putCharArray(keyPrefix, charArray);
    }

    @Override
    public String get(Bundle bundle, String data, String keyPrefix) {
        char[] inArray = bundle.getCharArray(keyPrefix);
        char[] outArray = new char[inArray.length - keyPrefix.length()];
        System.arraycopy(inArray, keyPrefix.length(), outArray, 0, outArray.length);
        return new String(outArray);
    }

    @Override
    public void put(Intent intent, String data, String keyPrefix) {
        char[] charArray = (keyPrefix + data).toCharArray();
        intent.putExtra(keyPrefix, charArray);
    }

    @Override
    public String get(Intent intent, String data, String keyPrefix) {
        char[] inArray = intent.getCharArrayExtra(keyPrefix);
        char[] outArray = new char[inArray.length - keyPrefix.length()];
        System.arraycopy(inArray, keyPrefix.length(), outArray, 0, outArray.length);
        return new String(outArray);
    }
}
