package pocketknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Denotes an interface method that will return an {@link android.content.Intent} with the variables as extras.
 * {@literal @}{@link pocketknife.Data} is used to specify a parameter that will be assigned
 * to the data element of the intent.
 *
 * <pre><code>
 *     {@literal @}IntentBuilder(action = Intent.ACTION_VIEW)
 *     Intent buildIntent({@literal @}Data String data, int i);
 * </code></pre>
 *
 * Generated code will look something like this
 * <pre><code>
 *     public static final String EXTRA_I = "EXTRA_I";
 *
 *     Intent buildIntent(String data, int i) {
 *         Intent intent = new Intent();
 *         intent.setAction(Intent.ACTION_VIEW);
 *         intent.setData(data);
 *         intent.putExtra(EXTRA_I, i);
 *         return intent;
 *     }
 * </code></pre>
 */
@Retention(CLASS)
@Target(METHOD)
public @interface IntentBuilder {
    int flags() default 0;
    Class cls() default Void.class;
    String action() default "";
    String[] categories() default { };
    String type() default "";
}