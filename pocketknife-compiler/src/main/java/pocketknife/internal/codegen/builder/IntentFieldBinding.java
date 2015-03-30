package pocketknife.internal.codegen.builder;

import pocketknife.internal.codegen.FieldBinding;

public class IntentFieldBinding extends FieldBinding {
    public static final String KEY_PREFIX = "EXTRA_";
    private String name;
    private String type;
    private String intentType;
    private String key;
    private boolean arrayList;

    public IntentFieldBinding(String name, String type, String intentType, String key, boolean arrayList) {
        this.name = name;
        this.type = type;
        this.intentType = intentType;
        this.key = key;
        this.arrayList = arrayList;
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntentFieldBinding && this.name.equals(((IntentFieldBinding) obj).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
