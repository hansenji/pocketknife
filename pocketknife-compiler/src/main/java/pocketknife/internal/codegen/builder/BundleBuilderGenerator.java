package pocketknife.internal.codegen.builder;

import android.os.Bundle;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import pocketknife.internal.codegen.BaseGenerator;
import pocketknife.internal.codegen.BundleFieldBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class BundleBuilderGenerator extends BaseGenerator {

    private static final String RETURN_VAR_NAME_ROOT = "bundle";

    private final String classPackage;
    private final String className;
    private final String interfaceName;

    private List<BundleBuilderMethodBinding> methods = new ArrayList<BundleBuilderMethodBinding>();

    public BundleBuilderGenerator(String classPackage, String className, String interfaceName) {
        this.classPackage = classPackage;
        this.className = className;
        this.interfaceName = interfaceName;
    }

    public String getFqcn() {
        return classPackage + "." + className;
    }

    public JavaFile generate() throws IOException {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addSuperinterface(ClassName.get(classPackage, interfaceName))
                .addModifiers(PUBLIC)
                .addAnnotation(getGeneratedAnnotationSpec(BundleBuilderGenerator.class));

        addKeys(classBuilder);

        addMethods(classBuilder);

        return JavaFile.builder(classPackage, classBuilder.build()).build();
    }

    private void addKeys(TypeSpec.Builder classBuilder) {
        Set<String> keys = new LinkedHashSet<String>();
        for (BundleBuilderMethodBinding method : methods) {
            keys.addAll(method.getKeys());
        }

        for (String key : keys) {
            classBuilder.addField(FieldSpec.builder(String.class, key, PUBLIC, STATIC, FINAL)
                    .initializer("$S", key)
                    .build());
        }
    }

    private void addMethods(TypeSpec.Builder classBuilder) {
        for (BundleBuilderMethodBinding method : methods) {
            addMethod(method, classBuilder);
        }
    }

    private void addMethod(BundleBuilderMethodBinding method, TypeSpec.Builder classBuilder) {
        String returnVarName = getReturnVarName(RETURN_VAR_NAME_ROOT, method);

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getName())
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(Bundle.class)
                .addStatement("$T $N = new $T()", Bundle.class, returnVarName, Bundle.class);

        for (BundleFieldBinding fieldBinding : method.getFields()) {
            methodBuilder.addParameter(ClassName.get(fieldBinding.getType()), fieldBinding.getName());
            methodBuilder.addStatement("$N.put$L($N, $N)", returnVarName, fieldBinding.getBundleType(), fieldBinding.getKey(), fieldBinding.getName());
        }

        methodBuilder.addStatement("return $N", returnVarName);

        classBuilder.addMethod(methodBuilder.build());
    }

    public void addMethod(BundleBuilderMethodBinding methodBinding) {
        methods.add(methodBinding);
    }

}
