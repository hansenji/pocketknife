package pocketknife.internal.codegen;

public class IntentFieldBinding extends FieldBinding {
    public static final String KEY_PREFIX = "EXTRA_";

    private final String name;
    private final String type;
    private final String intentType;
    private final String key;
    // Builder Only
    private final boolean arrayList;

    // Injector Only
    private final boolean required;
    private final boolean needsToBeCast;
    private final boolean hasDefault;

    public IntentFieldBinding(String name, String type, String intentType, String key, boolean arrayList) {
        this.name = name;
        this.type = type;
        this.intentType = intentType;
        this.key = key;
        this.arrayList = arrayList;

        this.required = false;
        this.needsToBeCast = false;
        this.hasDefault = false;
    }

    public IntentFieldBinding(String name, String type, String intentType, String key, Boolean needsToBeCast, boolean hasDefault, boolean required) {
        this.name = name;
        this.type = type;
        this.intentType = intentType;
        this.key = key;
        this.needsToBeCast = needsToBeCast;
        this.hasDefault = hasDefault;
        this.required = required;

        this.arrayList = false;
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntentFieldBinding && this.name.equals(((IntentFieldBinding) obj).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
