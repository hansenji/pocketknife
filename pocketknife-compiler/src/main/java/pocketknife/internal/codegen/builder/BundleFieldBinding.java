package pocketknife.internal.codegen.builder;

import pocketknife.internal.codegen.FieldBinding;

public class BundleFieldBinding extends FieldBinding {
    private final String name;
    private final String type;
    private final String bundleType;
    private final String key;

    public BundleFieldBinding(String name, String type, String bundleType, String key) {
        this.name = name;
        this.type = type;
        this.bundleType = bundleType;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getBundleType() {
        return bundleType;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BundleFieldBinding && this.name.equals(((BundleFieldBinding) obj).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
