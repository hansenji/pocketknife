package pocketknife.internal.codegen;

import com.squareup.javapoet.MethodSpec;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class MethodBinding {

    public abstract List<? extends FieldBinding> getFields();

    public abstract Set<KeySpec> getKeys();

    public abstract MethodSpec generateMethodSpec(TypeUtil typeUtil);

    protected String getReturnVarName(String returnVarNameRoot) {
        Set<String> fieldNames = new LinkedHashSet<String>();
        for (FieldBinding fieldBinding : getFields()) {
            fieldNames.add(fieldBinding.getName());
        }

        String returnVarName = returnVarNameRoot;
        int count = 0;
        while (fieldNames.contains(returnVarName)) {
            returnVarName = returnVarNameRoot + ++count;
        }
        return returnVarName;
    }
}
