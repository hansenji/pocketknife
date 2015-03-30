package pocketknife.internal.codegen.builder;

import com.squareup.javawriter.JavaWriter;
import com.squareup.javawriter.StringLiteral;
import pocketknife.internal.codegen.TypeUtil;

import javax.annotation.Generated;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class IntentBuilderGenerator {
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
        writer.emitAnnotation(Generated.class, getGeneratedMap());
        writer.beginType(className, "class", EnumSet.of(PUBLIC), null, interfaceName);
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

    private Map<String, Object> getGeneratedMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("value", StringLiteral.forValue(IntentBuilderGenerator.class.getName()));
        map.put("date", StringLiteral.forValue(new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.getDefault()).format(new Date())));
        return map;
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

        writer.emitStatement("Intent intent = new Intent()");
        if (method.getAction() != null) {
            writer.emitStatement("intent.setAction(%s)", StringLiteral.forValue(method.getAction()));
        }
        if (method.getData() != null && method.getType() != null) {
            writer.beginControlFlow("if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)");
            writer.emitStatement("intent.setDataAndTypeAndNormalize(Uri.parse(%s), %s)", StringLiteral.forValue(method.getData()),
                    StringLiteral.forValue(method.getType()));
            writer.nextControlFlow("else");
            writer.emitStatement("intent.setDataAndType(Uri.parse(%s), %s)", StringLiteral.forValue(method.getData()),
                    StringLiteral.forValue(method.getType()));
            writer.endControlFlow();
        } else if (method.getData() != null) {
            writer.beginControlFlow("if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)");
            writer.emitStatement("intent.setDataAndNormalize(Uri.parse(%s))", StringLiteral.forValue(method.getData()));
            writer.nextControlFlow("else");
            writer.emitStatement("intent.setData(Uri.parse(%s))", StringLiteral.forValue(method.getData()));
            writer.endControlFlow();
        } else if (method.getType() != null) {
            writer.beginControlFlow("if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)");
            writer.emitStatement("intent.setTypeAndNormalize(%s)", StringLiteral.forValue(method.getType()));
            writer.nextControlFlow("else");
            writer.emitStatement("intent.setType(%s)", StringLiteral.forValue(method.getType()));
            writer.endControlFlow();
        }
        if (method.getClassName() != null) {
            writer.emitStatement("intent.setClass(context, %s.class)", method.getClassName());
        }
        writer.emitStatement("intent.setFlags(%s)", method.getFlags());
        for (String category : method.getCategories()) {
            writer.emitStatement("intent.addCategory(%s)", StringLiteral.forValue(category));
        }

        for (IntentFieldBinding fieldBinding : method.getFields()) {
            writeFieldBinding(fieldBinding, writer);
        }

        writer.emitStatement("return intent");
        writer.endMethod();
    }

    private void writeFieldBinding(IntentFieldBinding fieldBinding, JavaWriter writer) throws IOException {
        if (fieldBinding.isArrayList()) {
            writer.emitStatement("intent.put%sExtra(%s, %s)", fieldBinding.getIntentType(), fieldBinding.getKey(), fieldBinding.getName());
        } else {
            writer.emitStatement("intent.putExtra(%s, %s)", fieldBinding.getKey(), fieldBinding.getName());
        }
    }

    public void addMethod(IntentBuilderMethodBinding methodBinding) {
        methods.add(methodBinding);
    }
}
