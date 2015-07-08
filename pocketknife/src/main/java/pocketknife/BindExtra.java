package pocketknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind {@link android.content.Intent} extra for key.
 * <pre>
 *     <code>
 *         {@literal @}BindExtra("extra_key") int i;
 *     </code>
 * </pre>
 */
@Retention(CLASS)
@Target(FIELD)
public @interface BindExtra {
    String value() default "";
}
