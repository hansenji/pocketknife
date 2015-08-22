package pocketknife;

import android.content.Intent;

public interface PocketKnifeIntentSerializer<T> {
    void put(Intent intent, T target, String keyPrefix);
    T get(Intent intent, T target, String keyPrefix);
}
