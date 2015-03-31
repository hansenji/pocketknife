package pocketknife.internal.codegen.builder;

import com.squareup.javawriter.JavaWriter;
import com.squareup.javawriter.StringLiteral;
import pocketknife.internal.codegen.BaseGenerator;
import pocketknife.internal.codegen.BundleFieldBinding;
import pocketknife.internal.codegen.TypeUtil;

import javax.annotation.Generated;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
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

    public void generate(JavaWriter writer) throws IOException {
        writer.emitPackage(classPackage);
        writer.emitImports(TypeUtil.BUNDLE);
        writer.emitEmptyLine();
        writer.emitAnnotation(Generated.class, getGeneratedMap(BundleBuilderGenerator.class));
        writer.beginType(className, "class", EnumSet.of(PUBLIC), null, interfaceName);

        writer.emitEmptyLine();
        writeKeys(writer);

        writeMethods(writer);

        writer.endType();
    }

    private void writeKeys(JavaWriter writer) throws IOException {
        Set<String> keys = new HashSet<String>();
        for (BundleBuilderMethodBinding method : methods) {
            keys.addAll(method.getKeys());
        }

        for (String key : keys) {
            writer.emitField("String", key, EnumSet.of(PUBLIC, STATIC, FINAL), StringLiteral.forValue(key).toString());
        }
        writer.emitEmptyLine();
    }

    private void writeMethods(JavaWriter writer) throws IOException {
        for (BundleBuilderMethodBinding method : methods) {
            writeMethod(method, writer);
            writer.emitEmptyLine();
        }
    }

    private void writeMethod(BundleBuilderMethodBinding method, JavaWriter writer) throws IOException {
        writer.emitAnnotation(Override.class);

        String returnVarName = getReturnVarName(RETURN_VAR_NAME_ROOT, method);

        writer.beginMethod("Bundle", method.getName(), EnumSet.of(PUBLIC), method.getWriterParameters(), null);

        writer.emitStatement("Bundle %s = new Bundle()", returnVarName);

        for (BundleFieldBinding field : method.getFields()) {
            writer.emitStatement("%s.put%s(%s, %s)", returnVarName, field.getBundleType(), field.getKey(), field.getName());
        }

        writer.emitStatement("return %s", returnVarName);

        writer.endMethod();
    }

    public void addMethod(BundleBuilderMethodBinding methodBinding) {
        methods.add(methodBinding);
    }

}
