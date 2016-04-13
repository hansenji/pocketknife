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
    public static final String IF_DOLLAR_T_VERSION_SDK_INT_DOLLAR_T_VERSION_CODES_JELLY_BEAN = "if ($T.VERSION.SDK_INT >= $T.VERSION_CODES.JELLY_BEAN)";

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

        for (IntentFieldBinding field : fields) {
            methodBuilder.addParameter(ClassName.get(field.getType()), field.getName());
            if (StringUtils.equals(field.getName(), dataParam)) {
                continue;  // Data is handled previously
            }
            addPutExtraStatement(methodBuilder, field, returnVarName);
        }

        methodBuilder.addStatement("return $N", returnVarName);

        return methodBuilder.build();
    }

    private void addPutExtraStatement(MethodSpec.Builder methodBuilder, IntentFieldBinding field, String returnVarName) {
        KeySpec key = field.getKey();
        if (field.getIntentSerializer() == null) {
            String stmt = "$N.put";
            if (field.isArrayList()) {
                stmt = stmt.concat("$LExtra(");
            } else {
                stmt = stmt.concat("Extra(");
            }
            String keyValue;
            if (StringUtils.isBlank(key.getName())) {
                keyValue = key.getValue();
                stmt = stmt.concat("$S");
            } else {
                keyValue = key.getName();
                stmt = stmt.concat("$N");
            }
            stmt = stmt.concat(", $N)");
            if (field.isArrayList()) {
                methodBuilder.addStatement(stmt, returnVarName, field.getIntentType(), keyValue, field.getName());
            } else {
                methodBuilder.addStatement(stmt, returnVarName, keyValue, field.getName());
            }
        } else {
            if (StringUtils.isBlank(key.getName())) {
                methodBuilder.addStatement("new $T().put($N, $N, $S)", field.getIntentSerializer(), returnVarName, field.getName(), key.getValue());
            } else {
                methodBuilder.addStatement("new $T().put($N, $N, $N)", field.getIntentSerializer(), returnVarName, field.getName(), key.getName());
            }
        }
    }

    private void addDataAndOrType(MethodSpec.Builder methodBuilder, String returnVarName, TypeUtil typeUtil) {
        if (dataParam != null && type != null) {
            methodBuilder.beginControlFlow(IF_DOLLAR_T_VERSION_SDK_INT_DOLLAR_T_VERSION_CODES_JELLY_BEAN, ClassName.get(typeUtil.buildType),
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
            methodBuilder.beginControlFlow(IF_DOLLAR_T_VERSION_SDK_INT_DOLLAR_T_VERSION_CODES_JELLY_BEAN, ClassName.get(typeUtil.buildType),
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
            methodBuilder.beginControlFlow(IF_DOLLAR_T_VERSION_SDK_INT_DOLLAR_T_VERSION_CODES_JELLY_BEAN, ClassName.get(typeUtil.buildType),
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
