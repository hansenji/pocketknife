package pocketknife.internal;

import android.content.Intent;

public interface IntentBinding<T> {
    void bindExtras(T target, Intent intent);
}
