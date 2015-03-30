package pocketknife.internal.codegen;

import java.util.List;

public abstract class MethodBinding {
    public abstract List<? extends FieldBinding> getFields();
}
