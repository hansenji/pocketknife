package pocketknife;

import android.os.Build;

/**
 * Save and restore field.
 * <pre>
 *     <code>
 *         {@literal @}InjectArgument(key=CONSTANT_KEY) int i;
 *         {@literal @}InjectArgument(key="Bundle_key", defaultValue="100") int i;
 *         {@literal @}InjectArgument(key=CONSTANT_KEY, defaultValue=CONSTANT, minSdk=Build.VERSION_CODES.HONEYCOMB_MR1) int i;
 *         {@literal @}InjectArgument(key="Bundle_key", defaultValue="\"DEFAULT\"", minSdk=Build.VERSION_CODES.HONEYCOMB_MR1) String s;
 *     </code>
 * </pre>
 */
public @interface InjectArgument {
    String key();
    String defaultValue() default "";
    int minSdk() default Build.VERSION_CODES.FROYO; // for string/charSequence defaults minSdk must be Build.VERSION_CODES.HONEYCOMB_MR1

}
