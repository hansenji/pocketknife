package pocketknife;

import android.os.Build;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Denote that the variable specified by the binding is not required to be present.
 *
 * <pre><code>
 * {@literal @}NotRequired @SaveState int i;
 * {@literal @}NotRequired(Build.VERSION_CODES.HONEYCOMB_MR1) @SaveState String s = "Default Value";
 * {@literal @}NotRequired @BindArgument("BundleKey") int i;
 * </code></pre>
 */
@Retention(CLASS)
@Target(FIELD)
public @interface NotRequired {
    int value() default Build.VERSION_CODES.FROYO; // for string/charSequence defaults minSdk must be Build.VERSION_CODES.HONEYCOMB_MR1
}
