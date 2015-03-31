package pocketknife.internal.codegen;

public abstract class FieldBinding {
    public abstract String getName();

    /** A description of the binding in human readable form (e.g., "field 'foo'"). */
    public abstract String getDescription();

}
