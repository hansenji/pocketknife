package pocketknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Specifies the parameter that is to be set to the data field of an {@link android.content.Intent}.
 * Must be assigned to either a {@link java.lang.String} or {@link android.net.Uri} on a method annotated with
 * {@literal @}{@link pocketknife.IntentBuilder}
 */
@Retention(CLASS)
@Target(PARAMETER)
public @interface Data {
}
