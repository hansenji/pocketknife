package pocketknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Denotes an interface method that will return an {@link android.content.Bundle} with the variables as arguments.
 * <pre><code>
 *     {@literal @}BundleBuilder
 *     Bundle buildBundle(int i);
 * </code></pre>
 *
 * Generated code will look something like this
 * <pre><code>
 *     public static final String ARG_I = "ARG_I";
 *
 *     Bundle buildBundle(int i) {
 *         Bundle bundle = new Bundle();
 *         intent.putExtra(ARG_I, i);
 *         return bundle;
 *     }
 * </code></pre>
 */
@Retention(CLASS)
@Target(METHOD)
public @interface BundleBuilder {
}
