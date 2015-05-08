package pocketknife.internal.codegen.builder;

import pocketknife.internal.codegen.BundleFieldBinding;
import pocketknife.internal.codegen.MethodBinding;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BundleBuilderMethodBinding extends MethodBinding {
    private final String name;

    private final List<BundleFieldBinding> fields = new ArrayList<BundleFieldBinding>();


    public BundleBuilderMethodBinding(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addField(BundleFieldBinding fieldBinding) {
        if (fields.contains(fieldBinding)) {
            throw new IllegalStateException("Cannot have multiple arguments named: " + fieldBinding.getName());
        }
        fields.add(fieldBinding);
    }

    public Set<String> getKeys() {
        Set<String> keys = new LinkedHashSet<String>();
        for (BundleFieldBinding field : fields) {
            keys.add(field.getKey());
        }
        return keys;
    }

    public List<BundleFieldBinding> getFields() {
        return fields;
    }
}
