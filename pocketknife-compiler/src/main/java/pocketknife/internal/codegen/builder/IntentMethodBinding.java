package pocketknife.internal.codegen.builder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.apache.commons.lang3.StringUtils;
import pocketknife.internal.codegen.IntentFieldBinding;
import pocketknife.internal.codegen.KeySpec;
import pocketknife.internal.codegen.MethodBinding;
import pocketknife.internal.codegen.TypeUtil;

import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.Modifier.PUBLIC;

public class IntentMethodBinding extends MethodBinding {

    private static final String RETURN_VAR_NAME_ROOT = "intent";

    private final String name;
    private final TypeMirror className;
    private final String action;
    private final String dataParam;
    private final Integer flags;
    private final String[] categories;
    private final String type;
    private final boolean dataParamIsString;

    private final List<IntentFieldBinding> fields = new ArrayList<IntentFieldBinding>();

    public IntentMethodBinding(String name, TypeMirror className, String action, String dataParam, Integer flags, String[] categories, String type,
                               boolean dataParamIsString) {
        this.name = name;
        this.className = className;
        this.action = action;
        this.dataParam = dataParam;
        this.flags = flags;
        this.categories = categories;
        this.type = type;
        this.dataParamIsString = dataParamIsString;
    }

    public void addField(IntentFieldBinding fieldBinding) {
        if (fields.contains(fieldBinding)) {
            throw new IllegalStateException("Cannot have multiple arguments named: " + fieldBinding.getName());
        }
        fields.add(fieldBinding);
    }

    @Override
    public Set<KeySpec> getKeys() {
        Set<KeySpec> keys = new LinkedHashSet<KeySpec>();
        for (IntentFieldBinding field : fields) {
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
                .returns(ClassName.get(typeUtil.intentType))
                .addStatement("$T $N = new $T()", ClassName.get(typeUtil.intentType), returnVarName, ClassName.get(typeUtil.intentType));
        if (action != null) {
            methodBuilder.addStatement("$N.setAction($S)", returnVarName, action);
        }
        addDataAndOrType(methodBuilder, returnVarName, typeUtil);
        if (className != null) {
            methodBuilder.addStatement("$N.setClass(this.context, $T.class)", returnVarName, ClassName.get(className));
        }
        if (flags != null) {
            methodBuilder.addStatement("$N.setFlags($L)", returnVarName, flags);
        }
        for (String category : categories) {
            methodBuilder.addStatement("$N.addCategory($S)", returnVarName, category);
        }

        for (IntentFieldBinding fieldBinding : fields) {
            methodBuilder.addParameter(ClassName.get(fieldBinding.getType()), fieldBinding.getName());
            if (StringUtils.equals(fieldBinding.getName(), dataParam)) {
                continue;  // Data is handled previously
            }
            String stmt = "$N.put";
            if (fieldBinding.isArrayList()) {
                stmt = stmt.concat("$LExtra(");
            } else {
                stmt = stmt.concat("Extra(");
            }
            String keyValue;
            KeySpec key = fieldBinding.getKey();
            if (StringUtils.isBlank(key.getName())) {
                keyValue = key.getValue();
                stmt = stmt.concat("$S");
            } else {
                keyValue = key.getName();
                stmt = stmt.concat("$N");
            }
            stmt = stmt.concat(", $N)");
            if (fieldBinding.isArrayList()) {
                methodBuilder.addStatement(stmt, returnVarName, fieldBinding.getIntentType(), keyValue, fieldBinding.getName());
            } else {
                methodBuilder.addStatement(stmt, returnVarName, keyValue, fieldBinding.getName());
            }
        }

        methodBuilder.addStatement("return $N", returnVarName);

        return methodBuilder.build();
    }

    private void addDataAndOrType(MethodSpec.Builder methodBuilder, String returnVarName, TypeUtil typeUtil) {
        if (dataParam != null && type != null) {
            methodBuilder.beginControlFlow("if ($T.VERSION.SDK_INT >= $T.VERSION_CODES.JELLY_BEAN)", ClassName.get(typeUtil.buildType),
                    ClassName.get(typeUtil.buildType));
            if (dataParamIsString) {
                methodBuilder.addStatement("$N.setDataAndTypeAndNormalize($T.parse($N), $S)", returnVarName, ClassName.get(typeUtil.uriType), dataParam, type);
            } else {
                methodBuilder.addStatement("$N.setDataAndTypeAndNormalize($N, $S)", returnVarName, dataParam, type);
            }
            methodBuilder.nextControlFlow("else");
            if (dataParamIsString) {
                methodBuilder.addStatement("$N.setDataAndType($T.parse($N), $S)", returnVarName, ClassName.get(typeUtil.uriType), dataParam, type);
            } else {
                methodBuilder.addStatement("$N.setDataAndType($N, $S)", returnVarName, dataParam, type);
            }
            methodBuilder.endControlFlow();
        } else if (dataParam != null) {
            methodBuilder.beginControlFlow("if ($T.VERSION.SDK_INT >= $T.VERSION_CODES.JELLY_BEAN)", ClassName.get(typeUtil.buildType),
                    ClassName.get(typeUtil.buildType));
            if (dataParamIsString) {
                methodBuilder.addStatement("$N.setDataAndNormalize($T.parse($N))", returnVarName, ClassName.get(typeUtil.uriType), dataParam);
            } else {
                methodBuilder.addStatement("$N.setDataAndNormalize($N)", returnVarName, dataParam);
            }
            methodBuilder.nextControlFlow("else");
            if (dataParamIsString) {
                methodBuilder.addStatement("$N.setData($T.parse($N))", returnVarName, ClassName.get(typeUtil.uriType), dataParam);
            } else {
                methodBuilder.addStatement("$N.setData($N)", returnVarName, dataParam);
            }
            methodBuilder.endControlFlow();
        } else if (type != null) {
            methodBuilder.beginControlFlow("if ($T.VERSION.SDK_INT >= $T.VERSION_CODES.JELLY_BEAN)", ClassName.get(typeUtil.buildType),
                    ClassName.get(typeUtil.buildType));
            methodBuilder.addStatement("$N.setTypeAndNormalize($S)", returnVarName, type);
            methodBuilder.nextControlFlow("else");
            methodBuilder.addStatement("$N.setType($S)", returnVarName, type);
            methodBuilder.endControlFlow();
        }
    }

    @Override
    public List<IntentFieldBinding> getFields() {
        return fields;
    }
}
