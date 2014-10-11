package pocketknife.internal;

import android.os.Bundle;


/**
 * Created by hansenji on 9/14/14.
 */
public abstract class BundleBinding<T> {
    public abstract void saveInstanceState(T target, Bundle bundle);

    public abstract void restoreInstanceState(T target, Bundle bundle);

    public abstract void injectArguments(T target, Bundle bundle);
}
