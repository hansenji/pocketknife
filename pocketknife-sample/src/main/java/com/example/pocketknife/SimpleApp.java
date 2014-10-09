package com.example.pocketknife;

import android.app.Application;
import pocketknife.PocketKnife;

public class SimpleApp extends Application {
  @Override public void onCreate() {
    super.onCreate();
    PocketKnife.setDebug(BuildConfig.DEBUG);
  }
}
