package pocketknife.internal.codegen.builder;

import com.squareup.javawriter.JavaWriter;
import com.squareup.javawriter.StringLiteral;
import pocketknife.internal.codegen.BaseGenerator;
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

    public void generate(JavaWriter writer) throws IOException {
        writer.emitPackage(classPackage);
        writer.emitImports(TypeUtil.INTENT, TypeUtil.CONTEXT, TypeUtil.BUILD, TypeUtil.URI);
        writer.emitEmptyLine();
        writer.emitAnnotation(Generated.class, getGeneratedMap(IntentBuilderGenerator.class));
        writer.beginType(className, "class", EnumSet.of(PUBLIC), null, interfaceName);
        writer.emitEmptyLine();
        writeKeys(writer);
        writer.emitField("Context", "context");
        writer.emitEmptyLine();
        writer.beginConstructor(EnumSet.of(PUBLIC), "Context", "context");
        writer.emitStatement("this.context = context");
        writer.endConstructor();
        writer.emitEmptyLine();

        writeMethods(writer);

        writer.endType();
    }

    private void writeKeys(JavaWriter writer) throws IOException {
        Set<String> keys = new HashSet<String>();
        for (IntentBuilderMethodBinding method : methods) {
            keys.addAll(method.getKeys());
        }

        for (String key : keys) {
            writer.emitField("String", key, EnumSet.of(PUBLIC, STATIC, FINAL), StringLiteral.forValue(key).toString());
        }
        writer.emitEmptyLine();
    }

    private void writeMethods(JavaWriter writer) throws IOException {
        for (IntentBuilderMethodBinding method : methods) {
            writeMethod(method, writer);
            writer.emitEmptyLine();
        }
    }

    private void writeMethod(IntentBuilderMethodBinding method, JavaWriter writer) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod("Intent", method.getName(), EnumSet.of(PUBLIC), method.getWriterParameters(), null);

        String returnVarName = getReturnVarName(RETURN_VAR_NAME_ROOT, method);

        writer.emitStatement("Intent %s = new Intent()", returnVarName);
        if (method.getAction() != null) {
            writer.emitStatement("%s.setAction(%s)", returnVarName, StringLiteral.forValue(method.getAction()));
        }
        if (method.getData() != null && method.getType() != null) {
            writer.beginControlFlow("if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)");
            writer.emitStatement("%s.setDataAndTypeAndNormalize(Uri.parse(%s), %s)", returnVarName, StringLiteral.forValue(method.getData()),
                    StringLiteral.forValue(method.getType()));
            writer.nextControlFlow("else");
            writer.emitStatement("%s.setDataAndType(Uri.parse(%s), %s)", returnVarName, StringLiteral.forValue(method.getData()),
                    StringLiteral.forValue(method.getType()));
            writer.endControlFlow();
        } else if (method.getData() != null) {
            writer.beginControlFlow("if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)");
            writer.emitStatement("%s.setDataAndNormalize(Uri.parse(%s))", returnVarName, StringLiteral.forValue(method.getData()));
            writer.nextControlFlow("else");
            writer.emitStatement("%s.setData(Uri.parse(%s))", returnVarName, StringLiteral.forValue(method.getData()));
            writer.endControlFlow();
        } else if (method.getType() != null) {
            writer.beginControlFlow("if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)");
            writer.emitStatement("%s.setTypeAndNormalize(%s)", returnVarName, StringLiteral.forValue(method.getType()));
            writer.nextControlFlow("else");
            writer.emitStatement("%s.setType(%s)", returnVarName, StringLiteral.forValue(method.getType()));
            writer.endControlFlow();
        }
        if (method.getClassName() != null) {
            writer.emitStatement("%s.setClass(context, %s.class)", returnVarName, method.getClassName());
        }
        writer.emitStatement("%s.setFlags(%s)", returnVarName, method.getFlags());
        for (String category : method.getCategories()) {
            writer.emitStatement("%s.addCategory(%s)", returnVarName, StringLiteral.forValue(category));
        }

        for (IntentFieldBinding fieldBinding : method.getFields()) {
            if (fieldBinding.isArrayList()) {
                writer.emitStatement("%s.put%sExtra(%s, %s)", returnVarName, fieldBinding.getIntentType(), fieldBinding.getKey(), fieldBinding.getName());
            } else {
                writer.emitStatement("%s.putExtra(%s, %s)", returnVarName, fieldBinding.getKey(), fieldBinding.getName());
            }
        }

        writer.emitStatement("return %s", returnVarName);
        writer.endMethod();
    }

    public void addMethod(IntentBuilderMethodBinding methodBinding) {
        methods.add(methodBinding);
    }
}
