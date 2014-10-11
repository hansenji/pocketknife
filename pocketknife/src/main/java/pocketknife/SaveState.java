package pocketknife;

import android.os.Build;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Save and restore field.
 * <pre>
 *     <code>
 *         {@literal @}SaveState int i;
 *         {@literal @}SaveState(defaultValue="100") int i;
 *         {@literal @}SaveState(defaultValue=CONSTANT, minSdk=Build.VERSION_CODES.HONEYCOMB_MR1) int i;
 *         {@literal @}SaveState(defaultValue="\"DEFAULT\"", minSdk=Build.VERSION_CODES.HONEYCOMB_MR1) String s;
 *     </code>
 * </pre>
 */
@Retention(CLASS)
@Target(FIELD)
public @interface SaveState {
    String defaultValue() default "";
    int minSdk() default Build.VERSION_CODES.FROYO; // for string/charSequence defaults minSdk must be Build.VERSION_CODES.HONEYCOMB_MR1
}

