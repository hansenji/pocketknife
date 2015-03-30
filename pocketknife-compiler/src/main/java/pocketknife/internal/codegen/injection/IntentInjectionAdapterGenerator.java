package pocketknife.internal.codegen.injection;

import com.squareup.javawriter.JavaWriter;
import pocketknife.internal.IntentBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static pocketknife.internal.GeneratedAdapters.INJECT_EXTRAS_METHOD;

public class IntentInjectionAdapterGenerator {

    private final Set<IntentInjectionFieldBinding> fields = new LinkedHashSet<IntentInjectionFieldBinding>();
    private final String classPackage;
    private final String className;
    private final String targetType;
    private String parentAdapter;


    public IntentInjectionAdapterGenerator(String classPackage, String className, String targetType) {
        this.classPackage = classPackage;
        this.className = className;
        this.targetType = targetType;
    }

    public void addField(IntentInjectionFieldBinding binding) {
        fields.add(binding);
    }

    public void generate(JavaWriter writer) throws IOException {
        writer.emitSingleLineComment(InjectionAdapterJavadoc.GENERATED_BY_POCKETKNIFE);
        writer.emitPackage(classPackage);
        writer.emitImports("android.content.Intent");
        writer.emitEmptyLine();
        if (parentAdapter != null) {
            writer.beginType(className + "<T extends " + targetType + ">", "class", EnumSet.of(PUBLIC), parentAdapter + "<T>");
        } else {
            writer.beginType(className + "<T extends " + targetType + ">", "class", EnumSet.of(PUBLIC), null, JavaWriter.type(IntentBinding.class, "T"));
        }
        writer.emitEmptyLine();

        writeIntentKeys(writer);

        // write injectExtras
        writeInjectExtras(writer);

        writer.endType();
    }

    private void writeIntentKeys(JavaWriter writer) throws IOException {
        writer.emitSingleLineComment(InjectionAdapterJavadoc.INTENT_KEYS);
        for (IntentInjectionFieldBinding field : fields) {
            writer.emitField(String.class.getCanonicalName(), field.getKey(), EnumSet.of(PRIVATE, STATIC, FINAL), JavaWriter.stringLiteral(field.getKey()));
        }
    }

    private void writeInjectExtras(JavaWriter writer) throws IOException {
        writer.emitJavadoc(InjectionAdapterJavadoc.INJECT_EXTRAS_METHOD, targetType);
        writer.beginMethod("void", INJECT_EXTRAS_METHOD, EnumSet.of(PUBLIC), "T", "target", "Intent", "intent");
        if (parentAdapter != null) {
            writer.emitStatement("super.%s(%s, %s)", INJECT_EXTRAS_METHOD, "target", "intent");
        }
        writer.beginControlFlow("if (intent == null)");
        writer.emitStatement("throw new IllegalStateException(\"Intent is null\")");
        writer.endControlFlow();
        for (IntentInjectionFieldBinding field : fields) {
            writeInjectExtraField(writer, field);
        }
        writer.endMethod();
    }

    private void writeInjectExtraField(JavaWriter writer, IntentInjectionFieldBinding field) throws IOException {
        writer.emitSingleLineComment(field.getDescription());
        if (field.isRequired()) {
            writeRequiredInjectExtraField(writer, field);
        } else {
            writeOptionalInjectExtraField(writer, field);
        }
    }

    private void writeRequiredInjectExtraField(JavaWriter writer, IntentInjectionFieldBinding field) throws IOException {
        List<String> stmtArgs = new ArrayList<String>();
        writer.beginControlFlow("if (intent.hasExtra(%s))", field.getKey());
        String stmt = "target.".concat(field.getName()).concat(" = ");
        if (field.needsToBeCast()) {
            stmt = stmt.concat("(%s) ");
            stmtArgs.add(field.getType());
        }
        stmt = stmt.concat("intent.get%sExtra(%s");
        stmtArgs.add(field.getIntentType());
        stmtArgs.add(field.getKey());
        if (field.hasDefault()) {
            stmt = stmt.concat(", target.").concat(field.getName());
        }
        stmt = stmt.concat(")");
        writer.emitStatement(stmt, stmtArgs.toArray(new Object[stmtArgs.size()]));
        writer.nextControlFlow("else");
        writer.emitStatement("throw new IllegalStateException(\"Required Extra with key '%s' was not found for '%s'. "
                + "If this field is not required add '@NotRequired' annotation\")", field.getKey(), field.getName());
        writer.endControlFlow();
    }

    private void writeOptionalInjectExtraField(JavaWriter writer, IntentInjectionFieldBinding field) throws IOException {
        List<String> stmtArgs = new ArrayList<String>();
        writer.beginControlFlow("if (intent.hasExtra(%s))", field.getKey());
        String stmt = "target.".concat(field.getName()).concat(" = ");
        if (field.needsToBeCast()) {
            stmt = stmt.concat("(%s) ");
            stmtArgs.add(field.getType());
        }
        stmt = stmt.concat("intent.get%sExtra(%s");
        stmtArgs.add(field.getIntentType());
        stmtArgs.add(field.getKey());
        if (field.hasDefault()) {
            stmt = stmt.concat(", target.").concat(field.getName());
        }
        stmt = stmt.concat(")");
        writer.emitStatement(stmt, stmtArgs.toArray(new Object[stmtArgs.size()]));
        writer.endControlFlow();
    }

    public CharSequence getFqcn() {
        return classPackage + "." + className;
    }

    public void setParentAdapter(String parentAdapter) {
        this.parentAdapter = parentAdapter;
    }
}
