package pocketknife.internal;

import android.os.Bundle;



public interface BundleBinding<T> {
    void saveInstanceState(T target, Bundle bundle);

    void restoreInstanceState(T target, Bundle bundle);

    void injectArguments(T target, Bundle bundle);
}
