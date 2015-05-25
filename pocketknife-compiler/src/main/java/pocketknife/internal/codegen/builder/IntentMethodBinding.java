package pocketknife.internal.codegen.builder;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import pocketknife.internal.codegen.IntentFieldBinding;
import pocketknife.internal.codegen.MethodBinding;

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
    private final String data;
    private final Integer flags;
    private final String[] categories;
    private final String type;

    private final List<IntentFieldBinding> fields = new ArrayList<IntentFieldBinding>();

    public IntentMethodBinding(String name, TypeMirror className, String action, String data, Integer flags, String[] categories, String type) {
        this.name = name;
        this.className = className;
        this.action = action;
        this.data = data;
        this.flags = flags;
        this.categories = categories;
        this.type = type;
    }

    public void addField(IntentFieldBinding fieldBinding) {
        if (fields.contains(fieldBinding)) {
            throw new IllegalStateException("Cannot have multiple arguments named: " + fieldBinding.getName());
        }
        fields.add(fieldBinding);
    }

    @Override
    public Set<String> getKeys() {
        Set<String> keys = new LinkedHashSet<String>();
        for (IntentFieldBinding field : fields) {
            keys.add(field.getKey());
        }
        return keys;
    }

    @Override
    public MethodSpec generateMethodSpec() {
        String returnVarName = getReturnVarName(RETURN_VAR_NAME_ROOT);

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(name)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(Intent.class)
                .addStatement("$T $N = new $T()", Intent.class, returnVarName, Intent.class);
        if (action != null) {
            methodBuilder.addStatement("$N.setAction($S)", returnVarName, action);
        }
        if (data != null && type != null) {
            methodBuilder.beginControlFlow("if ($T.VERSION.SDK_INT >= $T.VERSION_CODES.JELLY_BEAN)", Build.class, Build.class);
            methodBuilder.addStatement("$N.setDataAndTypeAndNormalize($T.parse($S), $S)", returnVarName, Uri.class, data, type);
            methodBuilder.nextControlFlow("else");
            methodBuilder.addStatement("$N.setDataAndType($T.parse($S), $S)", returnVarName, Uri.class, data, type);
            methodBuilder.endControlFlow();
        } else if (data != null) {
            methodBuilder.beginControlFlow("if ($T.VERSION.SDK_INT >= $T.VERSION_CODES.JELLY_BEAN)", Build.class, Build.class);
            methodBuilder.addStatement("$N.setDataAndNormalize($T.parse($S))", returnVarName, Uri.class, data);
            methodBuilder.nextControlFlow("else");
            methodBuilder.addStatement("$N.setData($T.parse($S))", returnVarName, Uri.class, data);
            methodBuilder.endControlFlow();
        } else if (type != null) {
            methodBuilder.beginControlFlow("if ($T.VERSION.SDK_INT >= $T.VERSION_CODES.JELLY_BEAN)", Build.class, Build.class);
            methodBuilder.addStatement("$N.setTypeAndNormalize($S)", returnVarName, type);
            methodBuilder.nextControlFlow("else");
            methodBuilder.addStatement("$N.setType($S)", returnVarName, type);
            methodBuilder.endControlFlow();
        }
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
            if (fieldBinding.isArrayList()) {
                methodBuilder.addStatement("$N.put$LExtra($N, $N)", returnVarName, fieldBinding.getIntentType(), fieldBinding.getKey(), fieldBinding.getName());
            } else {
                methodBuilder.addStatement("$N.putExtra($N, $N)", returnVarName, fieldBinding.getKey(), fieldBinding.getName());
            }
        }

        methodBuilder.addStatement("return $N", returnVarName);

        return methodBuilder.build();
    }

    @Override
    public List<IntentFieldBinding> getFields() {
        return fields;
    }
}
