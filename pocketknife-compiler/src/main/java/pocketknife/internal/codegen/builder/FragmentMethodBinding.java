package pocketknife.internal.codegen.builder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.apache.commons.lang3.StringUtils;
import pocketknife.internal.codegen.BundleFieldBinding;
import pocketknife.internal.codegen.FieldBinding;
import pocketknife.internal.codegen.KeySpec;
import pocketknife.internal.codegen.MethodBinding;
import pocketknife.internal.codegen.TypeUtil;

import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.Modifier.PUBLIC;

public class FragmentMethodBinding extends MethodBinding {

    private static final String RETURN_VAR_NAME_ROOT = "fragment";
    private static final String ARGS_VAR_NAME_ROOT = "args";

    private final String name;
    private final TypeMirror returnType;
    private final List<BundleFieldBinding> fields = new ArrayList<BundleFieldBinding>();


    public FragmentMethodBinding(String name, TypeMirror returnType) {
        this.name = name;
        this.returnType = returnType;
    }

    public void addField(BundleFieldBinding fieldBinding) {
        if (fields.contains(fieldBinding)) {
            throw new IllegalStateException("Cannot have multiple arguments named: " + fieldBinding.getName());
        }
        fields.add(fieldBinding);
    }

    @Override
    public List<? extends FieldBinding> getFields() {
        return fields;
    }

    @Override
    public Set<KeySpec> getKeys() {
        Set<KeySpec> keys = new LinkedHashSet<KeySpec>();
        for (BundleFieldBinding field : fields) {
            keys.add(field.getKey());
        }
        return keys;
    }

    @Override
    public MethodSpec generateMethodSpec(TypeUtil typeUtil) {
        String returnVarName = getReturnVarName(RETURN_VAR_NAME_ROOT);
        String argsVarName = getReturnVarName(ARGS_VAR_NAME_ROOT);

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(name)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(ClassName.get(returnType))
                .addStatement("$T $N = new $T()", ClassName.get(returnType), returnVarName, ClassName.get(returnType));
        if (!fields.isEmpty()) {
            methodBuilder.addStatement("$T $N = new $T()", ClassName.get(typeUtil.bundleType), argsVarName, ClassName.get(typeUtil.bundleType));
        }

        for (BundleFieldBinding fieldBinding : fields) {
            methodBuilder.addParameter(ClassName.get(fieldBinding.getType()), fieldBinding.getName());
            String keyValue;
            String stmt = "$N.put$L(";
            KeySpec key = fieldBinding.getKey();
            if (StringUtils.isBlank(key.getName())) {
                keyValue = key.getValue();
                stmt = stmt.concat("$S");
            } else {
                keyValue = key.getName();
                stmt = stmt.concat("$N");
            }
            stmt = stmt.concat(", $N)");
            methodBuilder.addStatement(stmt, argsVarName, fieldBinding.getBundleType(), keyValue, fieldBinding.getName());
        }

        if (!fields.isEmpty()) {
            methodBuilder.addStatement("$N.setArguments($N)", returnVarName, argsVarName);
        }

        methodBuilder.addStatement("return $N", returnVarName);

        return methodBuilder.build();
    }
}
