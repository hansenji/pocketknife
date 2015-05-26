package pocketknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Denotes an interface method that will return a fragment with the variables as arguments.
 *
 * <pre><code>
 *     {@literal @}FragmentBuilder
 *     Fragment buildFragment(int i);
 * </code></pre>
 *
 * Generated code will look something like this
 * <pre><code>
 *     public static final String ARG_I = "ARG_I";
 *
 *     Fragment buildFragment(int i) {
 *         Fragment fragment = new Fragment();
 *         Bundle args = new Bundle();
 *         args.putInt(ARG_I, i);
 *         fragment.setArguments(args);
 *         return fragment;
 *     }
 * </code></pre>
 */
@Retention(CLASS)
@Target(METHOD)
public @interface FragmentBuilder {
}
