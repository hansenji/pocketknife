package pocketknife.internal;

import android.os.Bundle;



public abstract class BundleBinding<T> {
    public abstract void saveInstanceState(T target, Bundle bundle);

    public abstract void restoreInstanceState(T target, Bundle bundle);

    public abstract void injectArguments(T target, Bundle bundle);
}
