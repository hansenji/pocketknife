package pocketknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Save and restore field.
 * <pre>
 *     <code>
 *         {@literal @}SaveState int i;
 *     </code>
 * </pre>
 */
@Retention(CLASS)
@Target(FIELD)
public @interface SaveState {
}

