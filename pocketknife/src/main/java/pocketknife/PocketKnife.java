package pocketknife;

import android.os.Bundle;
import android.util.Log;
import pocketknife.internal.BundleBinding;
import pocketknife.internal.Memoizer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import static pocketknife.internal.GeneratedAdapters.ANDROID_PREFIX;
import static pocketknife.internal.GeneratedAdapters.JAVA_PREFIX;
import static pocketknife.internal.GeneratedAdapters.BUNDLE_ADAPTER_SUFFIX;
import static pocketknife.internal.GeneratedAdapters.RESTORE_METHOD;
import static pocketknife.internal.GeneratedAdapters.SAVE_METHOD;


public final class PocketKnife {
    private static final String TAG = "PocketKnife";
    private static final Map<Class<?>, Method> SAVERS = new LinkedHashMap<Class<?>, Method>();
    private static final Map<Class<?>, Method> RESTORERS = new LinkedHashMap<Class<?>, Method>();
    private static final Method NO_OP = null;
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
    public static <T> T saveInstanceStateReflexive(T target, Bundle bundle) {
        Class<?> targetClass = target.getClass();
        try {
            if (debug) {
                Log.d(TAG, "Looking up backup adapter for " + targetClass.getName());
            }
            Method saveInstanceState = findSaverForClass(targetClass);
            if (saveInstanceState != null) {
                saveInstanceState.invoke(null, target, bundle);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            Throwable t = e;
            if (t instanceof InvocationTargetException) {
                t = t.getCause();
            }
            throw new RuntimeException("Unable to save state for " + target, t);
        }
        return target;
    }

    /**
     * Restore annotated fields in the specified {@code target} from the {@link Bundle}.
     *
     * @param target Target class to restore fields
     * @param bundle Bundle to restore field values.
     */
    public static <T> T restoreInstanceStateReflexive(T target, Bundle bundle) {
        Class<?> targetClass = target.getClass();
        try {
            if (debug) {
                Log.d(TAG, "Looking up backup adapter for " + targetClass.getName());
            }
            Method restoreInstanceState = findRestorerForClass(targetClass);
            if (restoreInstanceState != null) {
                restoreInstanceState.invoke(null, target, bundle);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            Throwable t = e;
            if (t instanceof InvocationTargetException) {
                t = t.getCause();
            }
            throw new RuntimeException("Unable to restore state for " + target, t);
        }
        return target;
    }

    private static Method findSaverForClass(Class<?> cls) throws NoSuchMethodException {
        Method save = SAVERS.get(cls);
        if (save != null) {
            if (debug) {
                Log.d(TAG, "HIT: Cached in saver map");
            }
            return save;
        }
        String clsName = cls.getName();
        if (clsName.startsWith(ANDROID_PREFIX) || clsName.startsWith(JAVA_PREFIX)) {
            if (debug) Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
            return NO_OP;
        }

        try {
            Class<?> bundleBinding = Class.forName(clsName.concat(BUNDLE_ADAPTER_SUFFIX));
            save = bundleBinding.getMethod(SAVE_METHOD, cls, Bundle.class);
            if (debug) {
                Log.d(TAG, "HIT: Class loaded bundleBinding class");
            }
        } catch (ClassNotFoundException e) {
            if (debug) {
                Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
            }
            save = findSaverForClass(cls.getSuperclass());
        }
        SAVERS.put(cls, save);
        return save;
    }

    private static Method findRestorerForClass(Class<?> cls) throws NoSuchMethodException {
        Method restore = RESTORERS.get(cls);
        if (restore != null) {
            if (debug) {
                Log.d(TAG, "HIT: Cached in restorer map");
            }
            return restore;
        }
        String clsName = cls.getName();
        if (clsName.startsWith(ANDROID_PREFIX) || clsName.startsWith(JAVA_PREFIX)) {
            if (debug) Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
            return NO_OP;
        }
        try {
            Class<?> bundleBinding = Class.forName(clsName.concat(BUNDLE_ADAPTER_SUFFIX));
            restore = bundleBinding.getMethod(RESTORE_METHOD, cls, Bundle.class);
            if (debug) {
                Log.d(TAG, "HIT: Class loaded bundleBinding class");
            }
        } catch (ClassNotFoundException e) {
            if (debug) {
                Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
            }
            restore = findRestorerForClass(cls.getSuperclass());
        }
        RESTORERS.put(cls, restore);
        return restore;
    }

    /**
     * Save annotated fields in the specified {@code target} to the {@link Bundle}.
     *
     * @param target Target class for field saving.
     * @param bundle Bundle to save the field values.
     */
    public static <T> T saveInstanceState(T target, Bundle bundle) {
        @SuppressWarnings("unchecked")
        BundleBinding<T> binding = (BundleBinding<T>) getBundleBinding(target.getClass().getClassLoader(), target.getClass().getName());
        binding.saveInstanceState(target, bundle);
        return target;
    }

    /**
     * Restore annotated fields in the specified {@code target} from the {@link Bundle}.
     *
     * @param target Target class to restore fields
     * @param bundle Bundle to restore field values.
     */
    public static <T> T restoreInstanceState(T target, Bundle bundle) {
        @SuppressWarnings("unchecked")
        BundleBinding<T> binding = (BundleBinding<T>) getBundleBinding(target.getClass().getClassLoader(), target.getClass().getName());
        binding.restoreInstanceState(target, bundle);
        return target;
    }

    /**
     * Inject annotated fields in the specified {@code fragment} from its arguments.
     *
     * @param fragment fragment to inject the arguments;
     */
    public static void injectArguments(android.app.Fragment fragment) {
       injectArguments(fragment, fragment.getArguments());
    }

    /**
     * Inject annotated fields in the specified {@code fragment} from its arguments.
     *
     * @param fragment fragment to inject the arguments;
     */
    public static void injectArguments(android.support.v4.app.Fragment fragment) {
        injectArguments(fragment, fragment.getArguments());
    }

    public static <T> T injectArguments(T target, Bundle bundle) {
        @SuppressWarnings("unchecked")
        BundleBinding<T> binding = (BundleBinding<T>) getBundleBinding(target.getClass().getClassLoader(), target.getClass().getName());
        binding.injectArguments(target, bundle);
        return target;
    }

    private static BundleBinding<?> getBundleBinding(ClassLoader classLoader, String className) {
        Class<?> adapterClass = loadClass(classLoader, className.concat(BUNDLE_ADAPTER_SUFFIX));
        if (!adapterClass.equals(Void.class)) {
            if (debug) {
                Log.d(TAG, "Found loadable adapter for " + className);
            }
            try {
                @SuppressWarnings("unchecked")
                Constructor<BundleBinding<?>> constructor = (Constructor<BundleBinding<?>>) adapterClass.getConstructor();
                return constructor.newInstance();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        "Couldn't find default constructor in the generated bundle adapter for class "
                                + className);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(
                        "Could not create an instance of the bundle adapter for class " + className, e);
            } catch (InstantiationException e) {
                throw new IllegalStateException(
                        "Could not create an instance of the bundle adapter for class " + className, e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                        "Could not create an instance of the bundle adapter for class " + className, e);
            }
        }
        if (debug) {
            Log.wtf(TAG, "Unable to find loadable adapter for " + className);
        }
        return null;
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
