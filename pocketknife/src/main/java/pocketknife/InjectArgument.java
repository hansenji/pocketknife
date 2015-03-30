package pocketknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Inject {@link android.os.Bundle} value for key.
 * <pre>
 *     <code>
 *         {@literal @}InjectArgument("Bundle_key") int i;
 *     </code>
 * </pre>
 */
@Retention(CLASS)
@Target(FIELD)
public @interface InjectArgument {
    String value() default "";
}
