package pocketknife.internal;

import android.content.Intent;

public abstract class IntentBinding<T> {
    public abstract void injectExtras(T target, Intent intent);
}
