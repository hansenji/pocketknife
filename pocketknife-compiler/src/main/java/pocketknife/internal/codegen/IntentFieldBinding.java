package pocketknife.internal.codegen;

import javax.lang.model.type.TypeMirror;

public class IntentFieldBinding extends FieldBinding {
    public static final String KEY_PREFIX = "EXTRA_";

    private final String name;
    private final TypeMirror type;
    private final String intentType;
    private final KeySpec key;
    private final TypeMirror intentSerializer;

    // Builder Only
    private final boolean arrayList;

    // Binder Only
    private final boolean required;
    private final boolean needsToBeCast;
    private final boolean hasDefault;

    public IntentFieldBinding(String name, TypeMirror type, String intentType, KeySpec key, boolean arrayList, TypeMirror intentSerializer) {
        this.name = name;
        this.type = type;
        this.intentType = intentType;
        this.key = key;
        this.intentSerializer = intentSerializer;
        this.arrayList = arrayList;

        this.required = false;
        this.needsToBeCast = false;
        this.hasDefault = false;
    }

    public IntentFieldBinding(String name, TypeMirror type, String intentType, KeySpec key, Boolean needsToBeCast, boolean hasDefault, boolean required,
                              TypeMirror intentSerializer) {
        this.name = name;
        this.type = type;
        this.intentType = intentType;
        this.key = key;
        this.needsToBeCast = needsToBeCast;
        this.hasDefault = hasDefault;
        this.required = required;
        this.intentSerializer = intentSerializer;

        this.arrayList = false;
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

    public KeySpec getKey() {
        return key;
    }

    public boolean isArrayList() {
        return arrayList;
    }

    public String getIntentType() {
        return intentType;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean needsToBeCast() {
        return needsToBeCast;
    }

    public boolean hasDefault() {
        return hasDefault;
    }

    public TypeMirror getIntentSerializer() {
        return intentSerializer;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntentFieldBinding && this.name.equals(((IntentFieldBinding) obj).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
