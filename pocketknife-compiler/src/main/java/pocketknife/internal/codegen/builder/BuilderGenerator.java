package pocketknife.internal.codegen.builder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import pocketknife.internal.codegen.BaseGenerator;
import pocketknife.internal.codegen.KeySpec;
import pocketknife.internal.codegen.MethodBinding;
import pocketknife.internal.codegen.TypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class BuilderGenerator extends BaseGenerator {
    private final String classPackage;
    private final String className;
    private final String interfaceName;

    private List<MethodBinding> methods = new ArrayList<MethodBinding>();
    private boolean contextRequired = false;

    public BuilderGenerator(String classPackage, String className, String interfaceName, TypeUtil typeUtil) {
        super(typeUtil);
        this.classPackage = classPackage;
        this.className = className;
        this.interfaceName = interfaceName;
    }

    public JavaFile generate() {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addSuperinterface(ClassName.get(classPackage, interfaceName))
                .addModifiers(PUBLIC)
                .addAnnotation(getGeneratedAnnotationSpec(BuilderGenerator.class));

        generateKeys(classBuilder);

        if (contextRequired) {
            classBuilder.addField(ClassName.get(typeUtil.contextType), "context", PRIVATE, FINAL);
            classBuilder.addMethod(MethodSpec.constructorBuilder()
                    .addModifiers(PUBLIC)
                    .addParameter(ClassName.get(typeUtil.contextType), "context")
                    .addStatement("this.$N = $N", "context", "context")
                    .build());
        }

        generateMethods(classBuilder);

        return JavaFile.builder(classPackage, classBuilder.build()).build();
    }

    private void generateKeys(TypeSpec.Builder classBuilder) {
        Set<KeySpec> keys = new TreeSet<KeySpec>();
        for (MethodBinding method : methods) {
            keys.addAll(method.getKeys());
        }

        for (KeySpec key : keys) {
            classBuilder.addField(FieldSpec.builder(String.class, key.getName(), PUBLIC, STATIC, FINAL)
                    .initializer("$S", key.getValue())
                    .build());
        }

    }

    private void generateMethods(TypeSpec.Builder classBuilder) {
        for (MethodBinding method : methods) {
            classBuilder.addMethod(method.generateMethodSpec(typeUtil));
        }
    }

    public void addMethod(MethodBinding method) {
        contextRequired |= method instanceof IntentMethodBinding;
        this.methods.add(method);
    }
}
