package pocketknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Inject {@link android.content.Intent} extra for key.
 * <pre>
 *     <code>
 *         {@literal @}InjectExtra("extra_key") int i;
 *     </code>
 * </pre>
 */
@Retention(CLASS)
@Target(FIELD)
public @interface InjectExtra {
    String value();
}
