package pocketknife.internal.codegen.builder;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class IntentBuilderMethodBinding {
    private final String name;
    private final String className;
    private final String action;
    private final String data;
    private final int flags;
    private final String[] categories;
    private final String type;

    private final List<IntentFieldBinding> fields = new ArrayList<IntentFieldBinding>();

    public IntentBuilderMethodBinding(String name, String className, String action, String data, int flags, String[] categories, String type) {
        this.name = name;
        if (void.class.getName().equals(className)) {
            this.className = null;
        } else {
            this.className = className;
        }
        this.action = Strings.emptyToNull(action);
        this.data = Strings.emptyToNull(data);
        this.flags = flags;
        this.categories = categories;
        this.type = Strings.emptyToNull(type);
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public String getAction() {
        return action;
    }

    public String getData() {
        return data;
    }

    public int getFlags() {
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

    public List<String> getWriterParameters() {
        List<String> params = new ArrayList<String>();
        for (IntentFieldBinding field : fields) {
            params.add(field.getType());
            params.add(field.getName());
        }
        return params;
    }

    public List<IntentFieldBinding> getFields() {
        return fields;
    }
}
