package pocketknife.internal.codegen;

import com.google.common.base.CaseFormat;

import static pocketknife.internal.GeneratedAdapters.BUNDLE_KEY_PREFIX;

public class BundleFieldBinding implements Binding {

    public enum AnnotationType {
        ARGUMENT, SAVE_STATE
    }

    private final AnnotationType annotationType;
    private final String name;
    private final String type;
    private final String bundleType;
    private final String key;
    private final boolean canHaveDefault;
    private final boolean required;
    private final boolean needsToBeCast;


    /**
     * This constructor is for the <code>{@literal @}SaveState</code> annotation;
     * @param name name of the variable
     * @param type type of the variable
     * @param bundleType type of bundle argument
     * @param needsToBeCast does variable need to be cast
     * @param canHaveDefault can type have a default value
     * @param required is key required to be present in the bundle
     */
    public BundleFieldBinding(String name, String type, String bundleType, boolean needsToBeCast, boolean canHaveDefault, boolean required) {
        this.annotationType = AnnotationType.SAVE_STATE;
        this.name = name;
        this.type = type;
        this.bundleType = bundleType;
        this.needsToBeCast = needsToBeCast;
        this.key = generateKey();
        this.canHaveDefault = canHaveDefault;
        this.required = required;
    }

    /**
     * This constructor is for the <code>{@literal @}InjectArgument</code> annotation;
     * @param name name of the variable
     * @param type type of the variable
     * @param bundleType type of bundle argument
     * @param key key of the bundle argument
     * @param needsToBeCast does variable need to be cast
     * @param canHaveDefault can type have a default value
     * @param required is key required to be present in the bundle
     */
    public BundleFieldBinding(String name, String type, String bundleType, String key, boolean needsToBeCast, boolean canHaveDefault, boolean required) {
        this.annotationType = AnnotationType.ARGUMENT;
        this.name = name;
        this.type = type;
        this.needsToBeCast = needsToBeCast;
        this.bundleType = bundleType;
        this.key = key;
        this.canHaveDefault = canHaveDefault;
        this.required = required;
    }

    @Override
    public String getDescription() {
        return "Field '" + type + " " + name + "'";
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public boolean isRequired() {
        return required;
    }

    public AnnotationType getAnnotationType() {
        return annotationType;
    }

    public String getBundleType() {
        return bundleType;
    }

    public boolean canHaveDefault() {
        return canHaveDefault;
    }

    public boolean needsToBeCast() {
        return needsToBeCast;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BundleFieldBinding
                && this.annotationType == ((BundleFieldBinding) obj).annotationType
                && this.name.equals(((BundleFieldBinding) obj).name);
    }

    @Override
    public int hashCode() {
        return (annotationType + name).hashCode();
    }

    private String generateKey() {
        return BUNDLE_KEY_PREFIX + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, getName());
    }
}
