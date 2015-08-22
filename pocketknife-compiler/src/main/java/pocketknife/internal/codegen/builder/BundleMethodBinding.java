package pocketknife.internal.codegen.builder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.apache.commons.lang3.StringUtils;
import pocketknife.internal.codegen.BundleFieldBinding;
import pocketknife.internal.codegen.KeySpec;
import pocketknife.internal.codegen.MethodBinding;
import pocketknife.internal.codegen.TypeUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.Modifier.PUBLIC;

public class BundleMethodBinding extends MethodBinding {

    private static final String RETURN_VAR_NAME_ROOT = "bundle";

    private final String name;

    private final List<BundleFieldBinding> fields = new ArrayList<BundleFieldBinding>();

    public BundleMethodBinding(String name) {
        this.name = name;
    }

    public void addField(BundleFieldBinding fieldBinding) {
        if (fields.contains(fieldBinding)) {
            throw new IllegalStateException("Cannot have multiple arguments named: " + fieldBinding.getName());
        }
        fields.add(fieldBinding);
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

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(name)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(ClassName.get(typeUtil.bundleType))
                .addStatement("$T $N = new $T()", ClassName.get(typeUtil.bundleType), returnVarName, ClassName.get(typeUtil.bundleType));

        for (BundleFieldBinding field : fields) {
            methodBuilder.addParameter(ClassName.get(field.getType()), field.getName());
            KeySpec key = field.getKey();
            if (field.getBundleSerializer() == null) {
                String keyValue;
                String stmt = "$N.put$L(";
                if (StringUtils.isBlank(key.getName())) {
                    keyValue = key.getValue();
                    stmt = stmt.concat("$S");
                } else {
                    keyValue = key.getName();
                    stmt = stmt.concat("$N");
                }
                stmt = stmt.concat(", $N)");
                methodBuilder.addStatement(stmt, returnVarName, field.getBundleType(), keyValue, field.getName());
            } else {
                if (StringUtils.isBlank(key.getName())) {
                    methodBuilder.addStatement("new $T().put($N, $N, $S)", field.getBundleSerializer(), returnVarName, field.getName(), key.getValue());
                } else {
                    methodBuilder.addStatement("new $T().put($N, $N, $N)", field.getBundleSerializer(), returnVarName, field.getName(), key.getName());
                }
            }
        }

        methodBuilder.addStatement("return $N", returnVarName);

        return methodBuilder.build();
    }

    @Override
    public List<BundleFieldBinding> getFields() {
        return fields;
    }
}
