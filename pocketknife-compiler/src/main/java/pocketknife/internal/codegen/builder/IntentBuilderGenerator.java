package pocketknife.internal.codegen.builder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import pocketknife.internal.codegen.BaseGenerator;
import pocketknife.internal.codegen.IntentFieldBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class IntentBuilderGenerator extends BaseGenerator {

    private static final String RETURN_VAR_NAME_ROOT = "intent";

    private final String classPackage;
    private final String className;
    private final String interfaceName;
    private List<IntentBuilderMethodBinding> methods = new ArrayList<IntentBuilderMethodBinding>();

    public IntentBuilderGenerator(String classPackage, String className, String interfaceName) {
        this.classPackage = classPackage;
        this.className = className;
        this.interfaceName = interfaceName;
    }

    public CharSequence getFqcn() {
        return classPackage + "." + className;
    }

    public JavaFile generate() throws IOException {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addSuperinterface(ClassName.get(classPackage, interfaceName))
                .addModifiers(PUBLIC)
                .addAnnotation(getGeneratedAnnotationSpec(IntentBuilderGenerator.class))
                .addField(Context.class, "context", PRIVATE, FINAL);

        addKeys(classBuilder);

        classBuilder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameter(Context.class, "context")
                .addStatement("this.$N = $N", "context", "context")
                .build());

        addMethods(classBuilder);

        return JavaFile.builder(classPackage, classBuilder.build()).build();
    }

    private void addKeys(TypeSpec.Builder classBuilder) {
        Set<String> keys = new LinkedHashSet<String>();
        for (IntentBuilderMethodBinding method : methods) {
            keys.addAll(method.getKeys());
        }

        for (String key : keys) {
            classBuilder.addField(FieldSpec.builder(String.class, key, PUBLIC, STATIC, FINAL)
                    .initializer("$S", key)
                    .build());
        }
    }

    private void addMethods(TypeSpec.Builder classBuilder) {
        for (IntentBuilderMethodBinding method : methods) {
            addMethod(method, classBuilder);
        }
    }

    private void addMethod(IntentBuilderMethodBinding method, TypeSpec.Builder classBuilder) {
        String returnVarName = getReturnVarName(RETURN_VAR_NAME_ROOT, method);

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getName())
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(Intent.class)
                .addStatement("$T $N = new $T()", Intent.class, returnVarName, Intent.class);
        if (method.getAction() != null) {
            methodBuilder.addStatement("$N.setAction($S)", returnVarName, method.getAction());
        }
        if (method.getData() != null && method.getType() != null) {
            methodBuilder.beginControlFlow("if ($T.VERSION.SDK_INT >= $T.VERSION_CODES.JELLY_BEAN)", Build.class, Build.class);
            methodBuilder.addStatement("$N.setDataAndTypeAndNormalize($T.parse($S), $S)", returnVarName, Uri.class, method.getData(), method.getType());
            methodBuilder.nextControlFlow("else");
            methodBuilder.addStatement("$N.setDataAndType($T.parse($S), $S)", returnVarName, Uri.class, method.getData(), method.getType());
            methodBuilder.endControlFlow();
        } else if (method.getData() != null) {
            methodBuilder.beginControlFlow("if ($T.VERSION.SDK_INT >= $T.VERSION_CODES.JELLY_BEAN)", Build.class, Build.class);
            methodBuilder.addStatement("$N.setDataAndNormalize($T.parse($S))", returnVarName, Uri.class, method.getData());
            methodBuilder.nextControlFlow("else");
            methodBuilder.addStatement("$N.setData($T.parse($S))", returnVarName, Uri.class, method.getData());
            methodBuilder.endControlFlow();
        } else if (method.getType() != null) {
            methodBuilder.beginControlFlow("if ($T.VERSION.SDK_INT >= $T.VERSION_CODES.JELLY_BEAN)", Build.class, Build.class);
            methodBuilder.addStatement("$N.setTypeAndNormalize($S)", returnVarName, method.getType());
            methodBuilder.nextControlFlow("else");
            methodBuilder.addStatement("$N.setType($S)", returnVarName, method.getType());
            methodBuilder.endControlFlow();
        }
        if (method.getClassName() != null) {
            methodBuilder.addStatement("$N.setClass(context, $T.class)", returnVarName, ClassName.get(method.getClassName()));
        }
        if (method.getFlags() != null) {
            methodBuilder.addStatement("$N.setFlags($L)", returnVarName, method.getFlags());
        }
        for (String category : method.getCategories()) {
            methodBuilder.addStatement("$N.addCategory($S)", returnVarName, category);
        }

        for (IntentFieldBinding fieldBinding : method.getFields()) {
            methodBuilder.addParameter(ClassName.get(fieldBinding.getType()), fieldBinding.getName());
            if (fieldBinding.isArrayList()) {
                methodBuilder.addStatement("$N.put$LExtra($N, $N)", returnVarName, fieldBinding.getIntentType(), fieldBinding.getKey(), fieldBinding.getName());
            } else {
                methodBuilder.addStatement("$N.putExtra($N, $N)", returnVarName, fieldBinding.getKey(), fieldBinding.getName());
            }
        }

        methodBuilder.addStatement("return $N", returnVarName);

        classBuilder.addMethod(methodBuilder.build());

    }

    public void addMethod(IntentBuilderMethodBinding methodBinding) {
        methods.add(methodBinding);
    }
}
