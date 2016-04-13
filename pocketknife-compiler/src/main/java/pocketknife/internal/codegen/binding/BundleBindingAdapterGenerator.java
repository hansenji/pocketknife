package pocketknife.internal.codegen.binding;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import pocketknife.internal.BundleBinding;
import pocketknife.internal.codegen.Access;
import pocketknife.internal.codegen.BaseGenerator;
import pocketknife.internal.codegen.BundleFieldBinding;
import pocketknife.internal.codegen.TypeUtil;

import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.Modifier.PUBLIC;
import static pocketknife.internal.codegen.BundleFieldBinding.AnnotationType.ARGUMENT;
import static pocketknife.internal.codegen.BundleFieldBinding.AnnotationType.SAVE_STATE;

public final class BundleBindingAdapterGenerator extends BaseGenerator {

    public static final String SAVE_METHOD = "saveInstanceState";
    public static final String RESTORE_METHOD = "restoreInstanceState";
    public static final String BIND_ARGUMENTS_METHOD = "bindArguments";

    private static final String BUNDLE = "bundle";
    private static final String TARGET = "target";
    public static final String SUPER_METHOD_TEMPLATE = "super.$L($N, $N)";
    public static final String THROW_NEW_DOLLAR_SIGN_T_PARENTHESES_DOLLAR_SIGN_S_PARENTHESES = "throw new $T($S)";

    private final Set<BundleFieldBinding> fields = new LinkedHashSet<BundleFieldBinding>();
    private final String classPackage;
    private final String className;
    private final TypeMirror targetType;
    private boolean required = false;
    private ClassName parentAdapter;

    public BundleBindingAdapterGenerator(String classPackage, String className, TypeMirror targetType, TypeUtil typeUtil) {
        super(typeUtil);
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
                .addJavadoc(getGeneratedComment(BundleBindingAdapterGenerator.class));

        if (parentAdapter != null) {
            classBuilder.superclass(ParameterizedTypeName.get(parentAdapter, t));
        } else {
            classBuilder.addSuperinterface(ParameterizedTypeName.get(ClassName.get(BundleBinding.class), t));
        }

        addSaveStateMethod(classBuilder, t);
        addRestoreStateMethod(classBuilder, t);
        addBindingArgumentsMethod(classBuilder, t);

        return JavaFile.builder(classPackage, classBuilder.build()).build();
    }

    private void addSaveStateMethod(TypeSpec.Builder classBuilder, TypeVariableName t) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(SAVE_METHOD)
                .addModifiers(PUBLIC)
                .addParameter(ParameterSpec.builder(t, TARGET).build())
                .addParameter(ParameterSpec.builder(ClassName.get(typeUtil.bundleType), BUNDLE).build());
        if (parentAdapter != null) {
            methodBuilder.addStatement(SUPER_METHOD_TEMPLATE, SAVE_METHOD, TARGET, BUNDLE);
        }
        for (BundleFieldBinding field : fields) {
            if (SAVE_STATE == field.getAnnotationType()) {
                Access access = field.getAccess();
                boolean method = access.getType() == Access.Type.METHOD;
                if (field.getBundleSerializer() == null) {
                    String stmt;
                    if (method) {
                        stmt = "$N.put$L($S, $N.$N())";
                    } else {
                        stmt = "$N.put$L($S, $N.$N)";
                    }
                    methodBuilder.addStatement(stmt, BUNDLE, field.getBundleType(), field.getKey().getValue(), TARGET, access.getGetter());
                } else {
                    String stmt;
                    if (method) {
                        stmt = "new $T().put($N, $N.$N(), $S)";
                    } else {
                        stmt = "new $T().put($N, $N.$N, $S)";
                    }
                    methodBuilder.addStatement(stmt, field.getBundleSerializer(), BUNDLE, TARGET, access.getGetter(),
                            field.getKey().getValue());
                }
            }
        }
        classBuilder.addMethod(methodBuilder.build());
    }

    private void addRestoreStateMethod(TypeSpec.Builder classBuilder, TypeVariableName t) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(RESTORE_METHOD)
                .addModifiers(PUBLIC)
                .addParameter(ParameterSpec.builder(t, TARGET).build())
                .addParameter(ParameterSpec.builder(ClassName.get(typeUtil.bundleType), BUNDLE).build());
        if (parentAdapter != null) {
            methodBuilder.addStatement(SUPER_METHOD_TEMPLATE, RESTORE_METHOD, TARGET, BUNDLE);
        }
        methodBuilder.beginControlFlow("if ($N != null)", BUNDLE);
        for (BundleFieldBinding field : fields) {
            if (SAVE_STATE == field.getAnnotationType()) {
                addGetStatement(methodBuilder, field);
            }
        }

        methodBuilder.endControlFlow();
        classBuilder.addMethod(methodBuilder.build());
    }

    private void addGetStatement(MethodSpec.Builder methodBuilder, BundleFieldBinding field) {
        Access access = field.getAccess();
        boolean method = access.getType() == Access.Type.METHOD;
        if (field.getBundleSerializer() == null) {
            methodBuilder.beginControlFlow("if ($N.containsKey($S))", BUNDLE, field.getKey().getValue());
            List<Object> stmtArgs = new ArrayList<Object>();
            String stmt;
            if (method) {
                stmt = "$N.$N(";
            } else {
                stmt = "$N.$N = ";
            }
            stmtArgs.add(TARGET);
            stmtArgs.add(access.getSetter());
            if (field.needsToBeCast()) {
                stmt = stmt.concat("($T)");
                stmtArgs.add(field.getType());
            }
            stmt = stmt.concat("$N.get$L($S");
            stmtArgs.add(BUNDLE);
            stmtArgs.add(field.getBundleType());
            stmtArgs.add(field.getKey().getValue());
            if (field.canHaveDefault()) {
                if (method) {
                    stmt = stmt.concat(", $N.$N()");
                } else {
                    stmt = stmt.concat(", $N.$N");
                }
                stmtArgs.add(TARGET);
                stmtArgs.add(access.getGetter());
            }
            stmt = stmt.concat(")");
            if (method) {
                stmt = stmt.concat(")");
            }
            methodBuilder.addStatement(stmt, stmtArgs.toArray(new Object[stmtArgs.size()]));
            if (field.isRequired()) {
                methodBuilder.nextControlFlow("else");
                methodBuilder.addStatement(THROW_NEW_DOLLAR_SIGN_T_PARENTHESES_DOLLAR_SIGN_S_PARENTHESES, IllegalStateException.class,
                        String.format("Required Bundle value with key '%s' was not found for '%s'. "
                                        + "If this field is not required add '@NotRequired' annotation",
                                field.getKey().getValue(), field.getName()));
            }
            methodBuilder.endControlFlow();
        } else {
            String stmt;
            if (method) {
                stmt = "$N.$N(new $T().get($N, $N.$N(), $S))";
            } else {
                stmt = "$N.$N = new $T().get($N, $N.$N, $S)";
            }
            methodBuilder.addStatement(stmt, TARGET, access.getSetter(), field.getBundleSerializer(), BUNDLE, TARGET,
                    access.getGetter(), field.getKey().getValue());
        }
    }

    private void addBindingArgumentsMethod(TypeSpec.Builder classBuilder, TypeVariableName t) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(BIND_ARGUMENTS_METHOD)
                .addModifiers(PUBLIC)
                .addParameter(ParameterSpec.builder(t, TARGET).build())
                .addParameter(ParameterSpec.builder(ClassName.get(typeUtil.bundleType), BUNDLE).build());
        if (parentAdapter != null) {
            methodBuilder.addStatement(SUPER_METHOD_TEMPLATE, BIND_ARGUMENTS_METHOD, TARGET, BUNDLE);
        }
        methodBuilder.beginControlFlow("if ($N == null)", BUNDLE);
        if (required) {
            methodBuilder.addStatement(THROW_NEW_DOLLAR_SIGN_T_PARENTHESES_DOLLAR_SIGN_S_PARENTHESES, IllegalStateException.class, "Argument bundle is null");
        } else {
            methodBuilder.addStatement("$N = new $T()", BUNDLE, ClassName.get(typeUtil.bundleType));
        }
        methodBuilder.endControlFlow();
        for (BundleFieldBinding field : fields) {
            if (ARGUMENT == field.getAnnotationType()) {
                addGetArgumentStatement(methodBuilder, field);
            }
        }
        classBuilder.addMethod(methodBuilder.build());
    }

    private void addGetArgumentStatement(MethodSpec.Builder methodBuilder, BundleFieldBinding field) {
        Access access = field.getAccess();
        boolean method = access.getType() == Access.Type.METHOD;
        if (field.getBundleSerializer() == null) {
            methodBuilder.beginControlFlow("if ($N.containsKey($S))", BUNDLE, field.getKey().getValue());
            List<Object> stmtArgs = new ArrayList<Object>();
            String stmt;
            if (method) {
                stmt = "$N.$N(";
            } else {
                stmt = "$N.$N = ";
            }
            stmtArgs.add(TARGET);
            stmtArgs.add(access.getSetter());
            if (field.needsToBeCast()) {
                stmt = stmt.concat("($T)");
                stmtArgs.add(field.getType());
            }
            stmt = stmt.concat("$N.get$L($S");
            stmtArgs.add(BUNDLE);
            stmtArgs.add(field.getBundleType());
            stmtArgs.add(field.getKey().getValue());
            if (field.canHaveDefault()) {
                if (method) {
                    stmt = stmt.concat(", $N.$N()");
                } else {
                    stmt = stmt.concat(", $N.$N");
                }
                stmtArgs.add(TARGET);
                stmtArgs.add(access.getGetter());
            }
            stmt = stmt.concat(")");
            if (method) {
                stmt = stmt.concat(")");
            }
            methodBuilder.addStatement(stmt, stmtArgs.toArray(new Object[stmtArgs.size()]));
            if (field.isRequired()) {
                methodBuilder.nextControlFlow("else");
                methodBuilder.addStatement(THROW_NEW_DOLLAR_SIGN_T_PARENTHESES_DOLLAR_SIGN_S_PARENTHESES, IllegalStateException.class,
                        String.format("Required Bundle value with key '%s' was not found for '%s'. "
                                        + "If this field is not required add '@NotRequired' annotation",
                                field.getKey().getValue(), field.getName()));
            }
            methodBuilder.endControlFlow();
        } else {
            String stmt;
            if (method) {
                stmt = "$N.$N(new $T().get($N, $N.$N(), $S))";
            } else {
                stmt = "$N.$N = new $T().get($N, $N.$N, $S)";
            }
            methodBuilder.addStatement(stmt, TARGET, access.getSetter(), field.getBundleSerializer(), BUNDLE, TARGET,
                    access.getGetter(), field.getKey().getValue());
        }
    }

    public void setParentAdapter(String packageName, String className) {
        this.parentAdapter = ClassName.get(packageName, className);
    }
}
