package pocketknife.internal.codegen.injection;

public class IntentInjectionFieldBinding implements FieldBinding {

    private final boolean required;
    private final String key;
    private final Boolean needsToBeCast;
    private final boolean hasDefault;
    private final String name;
    private final String type;
    private final String intentType;

    public IntentInjectionFieldBinding(String name, String type, String intentType, String key, Boolean needsToBeCast, boolean hasDefault, boolean required) {

        this.name = name;
        this.type = type;
        this.intentType = intentType;
        this.key = key;
        this.needsToBeCast = needsToBeCast;
        this.hasDefault = hasDefault;
        this.required = required;
    }

    @Override
    public String getDescription() {
        return "Field '" + type + " " + name + "'";
    }

    public boolean isRequired() {
        return required;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public boolean needsToBeCast() {
        return needsToBeCast;
    }

    public String getType() {
        return type;
    }

    public String getIntentType() {
        return intentType;
    }

    public boolean hasDefault() {
        return hasDefault;
    }
}
