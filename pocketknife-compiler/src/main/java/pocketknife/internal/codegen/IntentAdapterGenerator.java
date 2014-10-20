package pocketknife.internal.codegen;

import com.squareup.javawriter.JavaWriter;
import pocketknife.internal.GeneratedAdapters;
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

public class IntentAdapterGenerator {

    private final Set<IntentFieldBinding> fields = new LinkedHashSet<IntentFieldBinding>();
    private final String classPackage;
    private final String className;
    private final String targetType;

    public IntentAdapterGenerator(String classPackage, String className, String targetType) {
        this.classPackage = classPackage;
        this.className = className;
        this.targetType = targetType;
    }

    public void addField(IntentFieldBinding binding) {
        fields.add(binding);
    }

    public void generate(JavaWriter writer) throws IOException {
        writer.emitSingleLineComment(AdapterJavadoc.GENERATED_BY_POCKETKNIFE);
        writer.emitPackage(classPackage);
        writer.emitImports("android.content.Intent");
        writer.emitEmptyLine();
        writer.beginType(className, "class", EnumSet.of(PUBLIC, FINAL), JavaWriter.type(IntentBinding.class, targetType));
        writer.emitEmptyLine();

        writeIntentKeys(writer);

        // write injectExtras
        writeInjectExtras(writer);

        writer.endType();
    }

    private void writeIntentKeys(JavaWriter writer) throws IOException {
        writer.emitSingleLineComment(AdapterJavadoc.INTENT_KEYS);
        for (IntentFieldBinding field : fields) {
            writer.emitField(String.class.getCanonicalName(), field.getKey(), EnumSet.of(PRIVATE, STATIC, FINAL), JavaWriter.stringLiteral(field.getKey()));
        }
    }

    private void writeInjectExtras(JavaWriter writer) throws IOException {
        writer.emitJavadoc(AdapterJavadoc.INJECT_EXTRAS_METHOD, targetType);
        writer.beginMethod("void", GeneratedAdapters.INJECT_EXTRAS_METHOD, EnumSet.of(PUBLIC), targetType, "target", "Intent", "intent");
        writer.beginControlFlow("if (intent != null)");
        for (IntentFieldBinding field : fields) {
            writeInjectExtraField(writer, field);
        }
        writer.endControlFlow();
        writer.endMethod();
    }

    private void writeInjectExtraField(JavaWriter writer, IntentFieldBinding field) throws IOException {
        writer.emitSingleLineComment(field.getDescription());
        if (field.isRequired()) {
            writeRequiredInjectExtraField(writer, field);
        } else {
            writeOptionalInjectExtraField(writer, field);
        }
    }

    private void writeRequiredInjectExtraField(JavaWriter writer, IntentFieldBinding field) throws IOException {
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

    private void writeOptionalInjectExtraField(JavaWriter writer, IntentFieldBinding field) throws IOException {
        List<String> stmtArgs = new ArrayList<String>();
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
    }

    public CharSequence getFqcn() {
        return classPackage + "." + className;
    }
}
