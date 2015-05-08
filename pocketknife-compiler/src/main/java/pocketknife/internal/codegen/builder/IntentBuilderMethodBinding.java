package pocketknife.internal.codegen.builder;

import pocketknife.internal.codegen.IntentFieldBinding;
import pocketknife.internal.codegen.MethodBinding;

import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class IntentBuilderMethodBinding extends MethodBinding {
    private final String name;
    private final TypeMirror className;
    private final String action;
    private final String data;
    private final Integer flags;
    private final String[] categories;
    private final String type;

    private final List<IntentFieldBinding> fields = new ArrayList<IntentFieldBinding>();

    public IntentBuilderMethodBinding(String name, TypeMirror className, String action, String data, Integer flags, String[] categories, String type) {
        this.name = name;
        this.className = className;
        this.action = action;
        this.data = data;
        this.flags = flags;
        this.categories = categories;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public TypeMirror getClassName() {
        return className;
    }

    public String getAction() {
        return action;
    }

    public String getData() {
        return data;
    }

    public Integer getFlags() {
        return flags;
    }

    public String[] getCategories() {
        return categories;
    }

    public String getType() {
        return type;
    }

    public void addField(IntentFieldBinding fieldBinding) {
        if (fields.contains(fieldBinding)) {
            throw new IllegalStateException("Cannot have multiple arguments named: " + fieldBinding.getName());
        }
        fields.add(fieldBinding);
    }

    public Set<String> getKeys() {
        Set<String> keys = new LinkedHashSet<String>();
        for (IntentFieldBinding field : fields) {
            keys.add(field.getKey());
        }
        return keys;
    }

    public List<IntentFieldBinding> getFields() {
        return fields;
    }
}
