package pocketknife.internal.codegen.injection;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import pocketknife.internal.IntentBinding;
import pocketknife.internal.codegen.BaseGenerator;
import pocketknife.internal.codegen.IntentFieldBinding;
import pocketknife.internal.codegen.TypeUtil;

import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.Modifier.PUBLIC;

public class IntentInjectionAdapterGenerator extends BaseGenerator {

    public static final String INJECT_EXTRAS_METHOD = "injectExtras";

    private static final String TARGET = "target";
    private static final String INTENT = "intent";

    private final Set<IntentFieldBinding> fields = new LinkedHashSet<IntentFieldBinding>();
    private final String classPackage;
    private final String className;
    private final TypeMirror targetType;
    private ClassName parentAdapter;


    public IntentInjectionAdapterGenerator(String classPackage, String className, TypeMirror targetType, TypeUtil typeUtil) {
        super(typeUtil);
        this.classPackage = classPackage;
        this.className = className;
        this.targetType = targetType;
    }

    public void addField(IntentFieldBinding binding) {
        fields.add(binding);
    }

    public JavaFile generate() throws IOException {
        TypeVariableName t = TypeVariableName.get("T");
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addTypeVariable(TypeVariableName.get(t.name, ClassName.get(targetType)))
                .addModifiers(PUBLIC)
                .addAnnotation(getGeneratedAnnotationSpec(IntentInjectionAdapterGenerator.class));

        // Add Interface or Parent Class
        if (parentAdapter != null) {
            classBuilder.superclass(ParameterizedTypeName.get(parentAdapter, t));
        } else {
            classBuilder.addSuperinterface(ParameterizedTypeName.get(ClassName.get(IntentBinding.class), t));
        }

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(INJECT_EXTRAS_METHOD)
                .addModifiers(PUBLIC)
                .addParameter(ParameterSpec.builder(t, TARGET).build())
                .addParameter(ParameterSpec.builder(ClassName.get(typeUtil.intentType), INTENT).build());
        if (parentAdapter != null) {
            methodBuilder.addStatement("super.$L($N, $N)", INJECT_EXTRAS_METHOD, TARGET, INTENT);
        }
        methodBuilder.beginControlFlow("if ($N == null)", INTENT);
        methodBuilder.addStatement("throw new $T($S)", IllegalStateException.class, "intent is null");
        methodBuilder.endControlFlow();

        for (IntentFieldBinding field : fields) {
            addInjectExtraField(methodBuilder, field);
        }

        classBuilder.addMethod(methodBuilder.build());

        return JavaFile.builder(classPackage, classBuilder.build()).build();
    }

    private void addInjectExtraField(MethodSpec.Builder methodBuilder, IntentFieldBinding field) {
        methodBuilder.beginControlFlow("if ($N.hasExtra($S))", INTENT, field.getKey());

        List<Object> stmtArgs = new ArrayList<Object>();
        String stmt = "$N.$N = ";
        stmtArgs.add(TARGET);
        stmtArgs.add(field.getName());

        if (field.needsToBeCast()) {
            stmt = stmt.concat("($T)");
            stmtArgs.add(field.getType());
        }
        stmt = stmt.concat("$N.get$LExtra($S");
        stmtArgs.add(INTENT);
        stmtArgs.add(field.getIntentType());
        stmtArgs.add(field.getKey());

        if (field.hasDefault()) {
            stmt = stmt.concat(", $N.$N");
            stmtArgs.add(TARGET);
            stmtArgs.add(field.getName());
        }
        stmt = stmt.concat(")");
        methodBuilder.addStatement(stmt, stmtArgs.toArray(new Object[stmtArgs.size()]));
        if (field.isRequired()) {
            methodBuilder.nextControlFlow("else")
            .addStatement("throw new $T($S)", IllegalStateException.class, String.format("Required Extra with key '%s' was not found for '%s'."
                    + "If this is not required add '@NotRequired' annotation.", field.getKey(), field.getName()));
        }
        methodBuilder.endControlFlow();
    }

    public void setParentAdapter(String packageName, String className) {
        this.parentAdapter = ClassName.get(packageName, className);
    }
}
