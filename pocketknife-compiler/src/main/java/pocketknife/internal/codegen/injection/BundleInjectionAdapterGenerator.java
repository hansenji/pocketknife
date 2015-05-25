package pocketknife.internal.codegen.injection;

import android.os.Bundle;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import pocketknife.internal.BundleBinding;
import pocketknife.internal.codegen.BaseGenerator;
import pocketknife.internal.codegen.BundleFieldBinding;

import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.Modifier.PUBLIC;
import static pocketknife.internal.codegen.BundleFieldBinding.AnnotationType.ARGUMENT;
import static pocketknife.internal.codegen.BundleFieldBinding.AnnotationType.SAVE_STATE;

public final class BundleInjectionAdapterGenerator extends BaseGenerator {

    public static final String SAVE_METHOD = "saveInstanceState";
    public static final String RESTORE_METHOD = "restoreInstanceState";
    public static final String INJECT_ARGUMENTS_METHOD = "injectArguments";

    private static final String BUNDLE = "bundle";
    private static final String TARGET = "target";

    private final Set<BundleFieldBinding> fields = new LinkedHashSet<BundleFieldBinding>();
    private final String classPackage;
    private final String className;
    private final TypeMirror targetType;
    private boolean required = false;
    private ClassName parentAdapter;

    public BundleInjectionAdapterGenerator(String classPackage, String className, TypeMirror targetType) {
        this.classPackage = classPackage;
        this.className = className;
        this.targetType = targetType;
    }

    public void addField(BundleFieldBinding binding) {
        fields.add(binding);
    }

    public void orRequired(boolean required) {
        this.required |= required;
    }

    public JavaFile generate() throws IOException {
        TypeVariableName t = TypeVariableName.get("T");
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addTypeVariable(TypeVariableName.get(t.name, ClassName.get(targetType)))
                .addModifiers(PUBLIC)
                .addAnnotation(getGeneratedAnnotationSpec(BundleInjectionAdapterGenerator.class));

        if (parentAdapter != null) {
            classBuilder.superclass(ParameterizedTypeName.get(parentAdapter, t));
        } else {
            classBuilder.addSuperinterface(ParameterizedTypeName.get(ClassName.get(BundleBinding.class), t));
        }

        addSaveStateMethod(classBuilder, t);
        addRestoreStateMethod(classBuilder, t);
        addInjectArugmentsMethod(classBuilder, t);

        return JavaFile.builder(classPackage, classBuilder.build()).build();
    }

    private void addSaveStateMethod(TypeSpec.Builder classBuilder, TypeVariableName t) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(SAVE_METHOD)
                .addModifiers(PUBLIC)
                .addParameter(ParameterSpec.builder(t, TARGET).build())
                .addParameter(ParameterSpec.builder(Bundle.class, BUNDLE).build());
        if (parentAdapter != null) {
            methodBuilder.addStatement("super.$L($N, $N)", SAVE_METHOD, TARGET, BUNDLE);
        }
        for (BundleFieldBinding field : fields) {
            if (SAVE_STATE == field.getAnnotationType()) {
                methodBuilder.addStatement("$N.put$L($S, $N.$N)", BUNDLE, field.getBundleType(), field.getKey(), TARGET, field.getName());
            }
        }
        classBuilder.addMethod(methodBuilder.build());
    }

    private void addRestoreStateMethod(TypeSpec.Builder classBuilder, TypeVariableName t) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(RESTORE_METHOD)
                .addModifiers(PUBLIC)
                .addParameter(ParameterSpec.builder(t, TARGET).build())
                .addParameter(ParameterSpec.builder(Bundle.class, BUNDLE).build());
        if (parentAdapter != null) {
            methodBuilder.addStatement("super.$L($N, $N)", RESTORE_METHOD, TARGET, BUNDLE);
        }
        methodBuilder.beginControlFlow("if ($N != null)", BUNDLE);
        for (BundleFieldBinding field : fields) {
            if (SAVE_STATE == field.getAnnotationType()) {
                methodBuilder.beginControlFlow("if ($N.containsKey($S))", BUNDLE, field.getKey());
                List<Object> stmtArgs = new ArrayList<Object>();
                String stmt = "$N.$N = ";
                stmtArgs.add(TARGET);
                stmtArgs.add(field.getName());
                if (field.needsToBeCast()) {
                    stmt = stmt.concat("($T)");
                    stmtArgs.add(field.getType());
                }
                stmt = stmt.concat("$N.get$L($S");
                stmtArgs.add(BUNDLE);
                stmtArgs.add(field.getBundleType());
                stmtArgs.add(field.getKey());
                if (field.canHaveDefault()) {
                    stmt = stmt.concat(", $N.$N");
                    stmtArgs.add(TARGET);
                    stmtArgs.add(field.getName());
                }
                stmt = stmt.concat(")");
                methodBuilder.addStatement(stmt, stmtArgs.toArray(new Object[stmtArgs.size()]));
                if (field.isRequired()) {
                    methodBuilder.nextControlFlow("else");
                    methodBuilder.addStatement("throw new $T($S)", IllegalStateException.class,
                            String.format("Required Bundle value with key '%s' was not found for '%s'. "
                                            + "If this field is not required add '@NotRequired' annotation",
                                    field.getKey(), field.getName()));
                }
                methodBuilder.endControlFlow();
            }
        }

        methodBuilder.endControlFlow();
        classBuilder.addMethod(methodBuilder.build());
    }

    private void addInjectArugmentsMethod(TypeSpec.Builder classBuilder, TypeVariableName t) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(INJECT_ARGUMENTS_METHOD)
                .addModifiers(PUBLIC)
                .addParameter(ParameterSpec.builder(t, TARGET).build())
                .addParameter(ParameterSpec.builder(Bundle.class, BUNDLE).build());
        if (parentAdapter != null) {
            methodBuilder.addStatement("super.$L($N, $N)", INJECT_ARGUMENTS_METHOD, TARGET, BUNDLE);
        }
        methodBuilder.beginControlFlow("if ($N == null)", BUNDLE);
        if (required) {
            methodBuilder.addStatement("throw new $T($S)", IllegalStateException.class, "Argument bundle is null");
        } else {
            methodBuilder.addStatement("$N = new $T()", BUNDLE, Bundle.class);
        }
        methodBuilder.endControlFlow();
        for (BundleFieldBinding field : fields) {
            if (ARGUMENT == field.getAnnotationType()) {
                methodBuilder.beginControlFlow("if ($N.containsKey($S))", BUNDLE, field.getKey());
                List<Object> stmtArgs = new ArrayList<Object>();
                String stmt = "$N.$N = ";
                stmtArgs.add(TARGET);
                stmtArgs.add(field.getName());
                if (field.needsToBeCast()) {
                    stmt = stmt.concat("($T)");
                    stmtArgs.add(field.getType());
                }
                stmt = stmt.concat("$N.get$L($S");
                stmtArgs.add(BUNDLE);
                stmtArgs.add(field.getBundleType());
                stmtArgs.add(field.getKey());
                if (field.canHaveDefault()) {
                    stmt = stmt.concat(", $N.$N");
                    stmtArgs.add(TARGET);
                    stmtArgs.add(field.getName());
                }
                stmt = stmt.concat(")");
                methodBuilder.addStatement(stmt, stmtArgs.toArray(new Object[stmtArgs.size()]));
                if (field.isRequired()) {
                    methodBuilder.nextControlFlow("else");
                    methodBuilder.addStatement("throw new $T($S)", IllegalStateException.class,
                            String.format("Required Bundle value with key '%s' was not found for '%s'. "
                                            + "If this field is not required add '@NotRequired' annotation",
                                    field.getKey(), field.getName()));
                }
                methodBuilder.endControlFlow();
            }
        }
        classBuilder.addMethod(methodBuilder.build());
    }

    public void setParentAdapter(String packageName, String className) {
        this.parentAdapter = ClassName.get(packageName, className);
    }
}
