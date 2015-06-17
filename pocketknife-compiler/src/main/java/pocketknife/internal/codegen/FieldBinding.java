package pocketknife.internal.codegen;

import com.google.common.base.CaseFormat;

public abstract class FieldBinding {
    public abstract String getName();

    /** A description of the binding in human readable form (e.g., "field 'foo'"). */
    public abstract String getDescription();

    protected String generateKey(String prefix, String name) {
        return prefix + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }
}
