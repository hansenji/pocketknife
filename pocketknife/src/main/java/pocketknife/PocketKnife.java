package pocketknife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import pocketknife.internal.BundleBinding;
import pocketknife.internal.IntentBinding;
import pocketknife.internal.Memoizer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static pocketknife.internal.GeneratedAdapters.ANDROID_PREFIX;
import static pocketknife.internal.GeneratedAdapters.BUNDLE_ADAPTER_SUFFIX;
import static pocketknife.internal.GeneratedAdapters.INTENT_ADAPTER_SUFFIX;
import static pocketknife.internal.GeneratedAdapters.JAVA_PREFIX;


public final class PocketKnife {
    private static final String TAG = "PocketKnife";
    private static boolean debug;

    private PocketKnife() {
        throw new AssertionError("No instances.");
    }

    /**
     * Control whether debug logging ins enabled
     */
    public static void setDebug(boolean debug) {
        PocketKnife.debug = debug;
    }

    /**
     * Save annotated fields in the specified {@code target} to the {@link Bundle}.
     *
     * @param target Target class for field saving.
     * @param bundle Bundle to save the field values.
     */
    public static <T> void saveInstanceState(T target, Bundle bundle) {
        @SuppressWarnings("unchecked")
        BundleBinding<T> binding = (BundleBinding<T>) getBundleBinding(target.getClass().getClassLoader(), target.getClass());
        if (binding != null) {
            binding.saveInstanceState(target, bundle);
        }
    }

    /**
     * Restore annotated fields in the specified {@code target} from the {@link Bundle}.
     *
     * @param target Target class to restore fields
     * @param bundle Bundle to restore field values.
     */
    public static <T> void restoreInstanceState(T target, Bundle bundle) {
        @SuppressWarnings("unchecked")
        BundleBinding<T> binding = (BundleBinding<T>) getBundleBinding(target.getClass().getClassLoader(), target.getClass());
        if (binding != null) {
            binding.restoreInstanceState(target, bundle);
        }
    }

    /**
     * Inject annotated fields in the specified {@link android.app.Fragment} from its arguments.
     *
     * @param fragment fragment to inject the arguments;
     */
    public static void injectArguments(android.app.Fragment fragment) {
        injectArguments(fragment, fragment.getArguments());
    }

    /**
     * Inject annotated fields in the specified {@link android.support.v4.app.Fragment} from its arguments.
     *
     * @param fragment fragment to inject the arguments;
     */
    public static void injectArguments(android.support.v4.app.Fragment fragment) {
        injectArguments(fragment, fragment.getArguments());
    }

    /**
     * Inject annotated fields in the specified {@code target} from the {@link android.os.Bundle}.
     *
     * @param target Target object for inject arguments
     * @param bundle Bundle containing arguments;
     */
    public static <T> void injectArguments(T target, Bundle bundle) {
        @SuppressWarnings("unchecked")
        BundleBinding<T> binding = (BundleBinding<T>) getBundleBinding(target.getClass().getClassLoader(), target.getClass());
        if (binding != null) {
            binding.injectArguments(target, bundle);
        }
    }

    /**
     * Inject annotated field in the specified {@link android.app.Activity} from its intent.
     *
     * @param activity activity to inject the extras.
     */
    public static void injectExtras(Activity activity) {
        injectExtras(activity, activity.getIntent());
    }

    /**
     * Inject annotated fields in the specified {@code target} from the {@link android.content.Intent}.
     *
     * @param target Target object to inject the extras.
     * @param intent Intent containing the extras.
     */
    public static <T> void injectExtras(T target, Intent intent) {
        @SuppressWarnings("unchecked")
        IntentBinding<T> binding = (IntentBinding<T>) getIntentBinding(target.getClass().getClassLoader(), target.getClass());
        if (binding != null) {
            binding.injectExtras(target, intent);
        }
    }

    private static BundleBinding<?> getBundleBinding(ClassLoader classLoader, Class<?> cls) {
        String clsName = cls.getName();
        if (clsName.startsWith(ANDROID_PREFIX) || clsName.startsWith(JAVA_PREFIX)) {
            if (debug) {
                Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
            }
            return null;
        }
        Class<?> adapterClass = loadClass(classLoader, clsName.concat(BUNDLE_ADAPTER_SUFFIX));
        if (!adapterClass.equals(Void.class)) {
            if (debug) {
                Log.d(TAG, "Found loadable bundle adapter for " + clsName);
            }
            try {
                @SuppressWarnings("unchecked")
                Constructor<BundleBinding<?>> constructor = (Constructor<BundleBinding<?>>) adapterClass.getConstructor();
                return constructor.newInstance();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        "Couldn't find default constructor in the generated bundle adapter for class "
                                + clsName);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(
                        "Could not create an instance of the bundle adapter for class " + clsName, e);
            } catch (InstantiationException e) {
                throw new IllegalStateException(
                        "Could not create an instance of the bundle adapter for class " + clsName, e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                        "Could not create an instance of the bundle adapter for class " + clsName, e);
            }
        }
        // Search for Parent Class adapter
        if (debug) {
            Log.d(TAG, String.format("%s not found. Trying superclass %s", clsName, cls.getSuperclass().getName()));
        }
        return getBundleBinding(classLoader, cls.getSuperclass());
    }

    private static IntentBinding<?> getIntentBinding(ClassLoader classLoader, Class cls) {
        String clsName = cls.getName();
        if (clsName.startsWith(ANDROID_PREFIX) || clsName.startsWith(JAVA_PREFIX)) {
            if (debug) {
                Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
            }
            return null;
        }
        Class<?> adapterClass = loadClass(classLoader, clsName.concat(INTENT_ADAPTER_SUFFIX));
        if (!adapterClass.equals(Void.class)) {
            if (debug) {
                Log.d(TAG, "Found loadable intent adapter for " + clsName);
            }
            try {
                @SuppressWarnings("unchecked")
                Constructor<IntentBinding<?>> constructor = (Constructor<IntentBinding<?>>) adapterClass.getConstructor();
                return constructor.newInstance();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        "Couldn't find default constructor in the generated intent adapter for class "
                                + clsName);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(
                        "Could not create an instance of the intent adapter for class " + clsName, e);
            } catch (InstantiationException e) {
                throw new IllegalStateException(
                        "Could not create an instance of the intent adapter for class " + clsName, e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                        "Could not create an instance of the intent adapter for class " + clsName, e);
            }
        }
        // Search for Parent Class adapter
        if (debug) {
            Log.d(TAG, String.format("%s not found. Trying superclass %s", clsName, cls.getSuperclass().getName()));
        }
        return getIntentBinding(classLoader, cls.getSuperclass());
    }

    private static Class<?> loadClass(ClassLoader classLoader, String name) {
        // a null classloader is the system classloader.
        if (classLoader == null) {
            if (debug) {
                Log.d(TAG, "Class loader is null using system class loader");
            }
            classLoader = ClassLoader.getSystemClassLoader();
        }
        return CACHES.get(classLoader).get(name);
    }

    private static final Memoizer<ClassLoader, Memoizer<String, Class<?>>> CACHES = new Memoizer<ClassLoader, Memoizer<String, Class<?>>>() {
        @Override
        protected Memoizer<String, Class<?>> create(final ClassLoader classLoader) {
            return new Memoizer<String, Class<?>>() {
                @Override
                protected Class<?> create(String className) {
                    try {
                        Class<?> cls = classLoader.loadClass(className);
                        if (debug) {
                            Log.d(TAG, "Successfully loaded class " + className);
                        }
                        return cls;
                    } catch (ClassNotFoundException e) {
                        if (debug) {
                            Log.d(TAG, "Failed to load class " + className);
                        }
                        return Void.class; // Cache the failure (negative case).
                    }
                }
            };
        }
    };
}
