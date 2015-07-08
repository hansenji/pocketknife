package pocketknife.internal.codegen;

import javax.lang.model.type.TypeMirror;

public class BundleFieldBinding extends FieldBinding {
    public static final String ARGUMENT_KEY_PREFIX = "ARG_";
    public static final String SAVE_STATE_KEY_PREFIX = "BUNDLE_";


    public enum AnnotationType {
        ARGUMENT, BUILDER, SAVE_STATE
    }

    private final AnnotationType annotationType;
    private final String name;
    private final TypeMirror type;
    private final String bundleType;
    private final KeySpec key;

    // Binder Only
    private final boolean needsToBeCast;
    private final boolean canHaveDefault;
    private final boolean required;

    public BundleFieldBinding(String name, TypeMirror type, String bundleType, KeySpec key) {
        this(AnnotationType.BUILDER, name, type, bundleType, key, false, false, false);
    }

    public BundleFieldBinding(AnnotationType annotationType, String name, TypeMirror type, String bundleType, KeySpec key, boolean needsToBeCast,
                              boolean canHaveDefault, boolean required) {
        this.annotationType = annotationType;
        this.name = name;
        this.type = type;
        this.bundleType = bundleType;
        this.needsToBeCast = needsToBeCast;
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

    public TypeMirror getType() {
        return type;
    }

    public String getBundleType() {
        return bundleType;
    }

    public KeySpec getKey() {
        return key;
    }

    public AnnotationType getAnnotationType() {
        return annotationType;
    }

    public boolean isRequired() {
        return required;
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
}
