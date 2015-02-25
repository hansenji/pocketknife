package pocketknife.internal;

import android.content.Intent;

public interface IntentBinding<T> {
    void injectExtras(T target, Intent intent);
}
