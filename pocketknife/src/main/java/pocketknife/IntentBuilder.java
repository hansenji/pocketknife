package pocketknife;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Retention(CLASS)
@Target(METHOD)
public @interface IntentBuilder {
    int flags() default 0;
    Class cls() default Void.class;
    String action() default "";
    String data() default "";
    String[] categories() default { };
    String type() default "";
}