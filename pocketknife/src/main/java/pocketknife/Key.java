package pocketknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Specifies the key that is to be used for the {@link android.content.Intent} extra or the {@link android.os.Bundle} argument.
 * See {@literal @}{@link pocketknife.IntentBuilder}, {@literal @}{@link pocketknife.BundleBuilder}, or {@literal @}{@link pocketknife.FragmentBuilder}
 *
 * <pre><code>
 *     {@literal @}{@link pocketknife.IntentBuilder}(action = {@link android.content.Intent.ACTION_SEND})
 *     Intent buildIntent({@literal @}Key({@link android.content.Intent.EXTRA_EMAIL}) String email);
 * </code></pre>
 */
@Retention(CLASS)
@Target(PARAMETER)
public @interface Key {
    String value();
}
