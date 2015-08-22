package pocketknife;

import android.os.Bundle;

public interface PocketKnifeBundleSerializer<T> {

    void put(Bundle bundle, T target, String keyPrefix);

    T get(Bundle bundle, T target, String keyPrefix);
}
