package pocketknife.internal.codegen.injection;

import com.squareup.javawriter.JavaWriter;
import com.squareup.javawriter.StringLiteral;
import pocketknife.internal.BundleBinding;
import pocketknife.internal.codegen.BundleFieldBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.Modifier.PUBLIC;
import static pocketknife.internal.GeneratedAdapters.INJECT_ARGUMENTS_METHOD;
import static pocketknife.internal.GeneratedAdapters.RESTORE_METHOD;
import static pocketknife.internal.GeneratedAdapters.SAVE_METHOD;
import static pocketknife.internal.codegen.BundleFieldBinding.AnnotationType.ARGUMENT;
import static pocketknife.internal.codegen.BundleFieldBinding.AnnotationType.SAVE_STATE;

public final class BundleInjectionAdapterGenerator {

    private final Set<BundleFieldBinding> fields = new LinkedHashSet<BundleFieldBinding>();
    private final String classPackage;
    private final String className;
    private final String targetType;
    private boolean required = false;
    private String parentAdapter;

    public BundleInjectionAdapterGenerator(String classPackage, String className, String targetType) {
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

    public void generate(JavaWriter writer) throws IOException {
        writer.emitSingleLineComment(InjectionAdapterJavadoc.GENERATED_BY_POCKETKNIFE);
        writer.emitPackage(classPackage);
        writer.emitImports("android.os.Bundle");
        writer.emitEmptyLine();
        if (parentAdapter != null) {
            writer.beginType(className + "<T extends " + targetType + ">", "class", EnumSet.of(PUBLIC), parentAdapter + "<T>");
        } else {
            writer.beginType(className + "<T extends " + targetType + ">", "class", EnumSet.of(PUBLIC), null, JavaWriter.type(BundleBinding.class, "T"));
        }
        writer.emitEmptyLine();
        // write Save State
        writeSaveState(writer);
        writer.emitEmptyLine();
        // Write restoreState
        writeRestoreState(writer);
        writer.emitEmptyLine();
        // write injectArguments
        writeInjectArguments(writer);
        writer.endType();
    }

    private void writeSaveState(JavaWriter writer) throws IOException {
        writer.emitJavadoc(InjectionAdapterJavadoc.SAVE_INSTANCE_STATE_METHOD, targetType);
        writer.beginMethod("void", SAVE_METHOD, EnumSet.of(PUBLIC), "T", "target", "Bundle", "bundle");
        if (parentAdapter != null) {
            writer.emitStatement("super.%s(%s, %s)", SAVE_METHOD, "target", "bundle");
        }
        for (BundleFieldBinding field : fields) {
            if (SAVE_STATE == field.getAnnotationType()) {
                writeSaveFieldState(writer, field);
            }
        }
        writer.endMethod();
    }

    private void writeSaveFieldState(JavaWriter writer, BundleFieldBinding field) throws IOException {
        writer.emitSingleLineComment(field.getDescription());
        writer.emitStatement("bundle.put%s(%s, target.%s)", field.getBundleType(), StringLiteral.forValue(field.getKey()).toString(), field.getName());
    }

    private void writeRestoreState(JavaWriter writer) throws IOException {
        writer.emitJavadoc(InjectionAdapterJavadoc.RESTORE_INSTANCE_STATE_METHOD, targetType);
        writer.beginMethod("void", RESTORE_METHOD, EnumSet.of(PUBLIC), "T", "target", "Bundle", "bundle");
        if (parentAdapter != null) {
            writer.emitStatement("super.%s(%s, %s)", RESTORE_METHOD, "target", "bundle");
        }
        writer.beginControlFlow("if (bundle != null)");
        for (BundleFieldBinding field : fields) {
            if (SAVE_STATE == field.getAnnotationType()) {
                writeRestoreFieldState(writer, field);
            }
        }
        writer.endControlFlow();
        writer.endMethod();
    }

    private void writeRestoreFieldState(JavaWriter writer, BundleFieldBinding field) throws IOException {
        writer.emitSingleLineComment(field.getDescription());
        if (field.isRequired()) {
            writeRequiredRestoreFieldState(writer, field);
        } else {
            writeOptionalRestoreFieldState(writer, field);
        }
    }

    private void writeRequiredRestoreFieldState(JavaWriter writer, BundleFieldBinding field) throws IOException {
        List<String> stmtArgs = new ArrayList<String>();
        writer.beginControlFlow("if (bundle.containsKey(%s))", StringLiteral.forValue(field.getKey()).toString());
        String stmt = "target.".concat(field.getName()).concat(" = ");
        if (field.needsToBeCast()) {
            stmt = stmt.concat(("(%s) "));
            stmtArgs.add(field.getType());
        }
        stmt = stmt.concat("bundle.get%s(%s)");
        stmtArgs.add(field.getBundleType());
        stmtArgs.add(StringLiteral.forValue(field.getKey()).toString());
        writer.emitStatement(stmt, stmtArgs.toArray(new Object[stmtArgs.size()]));
        writer.nextControlFlow("else");
        writer.emitStatement("throw new IllegalStateException(\"Required Bundle value with key '%s' was not found for '%s'. "
                + "If this field is not required add '@NotRequired' annotation\")", field.getKey(), field.getName());
        writer.endControlFlow();
    }

    private void writeOptionalRestoreFieldState(JavaWriter writer, BundleFieldBinding field) throws IOException {
        List<String> stmtArgs = new ArrayList<String>();
        String stmt = "target.".concat(field.getName()).concat(" = ");
        if (field.needsToBeCast()) {
            stmt = stmt.concat("(%s) ");
            stmtArgs.add(field.getType());
        }
        stmt = stmt.concat("bundle.get%s(%s");
        stmtArgs.add(field.getBundleType());
        stmtArgs.add(StringLiteral.forValue(field.getKey()).toString());
        if (field.canHaveDefault()) {
            stmt = stmt.concat(", target.").concat(field.getName());
        }
        stmt = stmt.concat(")");
        writer.emitStatement(stmt, stmtArgs.toArray(new Object[stmtArgs.size()]));
    }

    private void writeInjectArguments(JavaWriter writer) throws IOException {
        writer.emitJavadoc(InjectionAdapterJavadoc.INJECT_ARGUMENTS_METHOD, targetType);
        writer.beginMethod("void", INJECT_ARGUMENTS_METHOD, EnumSet.of(PUBLIC), "T", "target", "Bundle", "bundle");
        if (parentAdapter != null) {
            writer.emitStatement("super.%s(%s, %s)", INJECT_ARGUMENTS_METHOD, "target", "bundle");
        }
        writer.beginControlFlow("if (bundle == null)");
        if (required) {
            writer.emitStatement("throw new IllegalStateException(\"Argument bundle is null\")");
        } else {
            writer.emitStatement("bundle = new Bundle()");
        }
        writer.endControlFlow();
        for (BundleFieldBinding field : fields) {
            if (ARGUMENT == field.getAnnotationType()) {
                writeInjectArgumentField(writer, field);
            }
        }
        writer.endMethod();
    }

    private void writeInjectArgumentField(JavaWriter writer, BundleFieldBinding field) throws IOException {
        writer.emitSingleLineComment(field.getDescription());
        if (field.isRequired()) {
            writeRequiredInjectArgumentField(writer, field);
        } else {
            writeOptionalInjectArgumentField(writer, field);
        }
    }

    private void writeRequiredInjectArgumentField(JavaWriter writer, BundleFieldBinding field) throws IOException {
        List<String> stmtArgs = new ArrayList<String>();
        writer.beginControlFlow("if (bundle.containsKey(%s))", StringLiteral.forValue(field.getKey()).toString());
        String stmt = "target.".concat(field.getName()).concat(" = ");
        if (field.needsToBeCast()) {
            stmt = stmt.concat("(%s) ");
            stmtArgs.add(field.getType());
        }
        stmt = stmt.concat("bundle.get%s(%s)");
        stmtArgs.add(field.getBundleType());
        stmtArgs.add(StringLiteral.forValue(field.getKey()).toString());
        writer.emitStatement(stmt, stmtArgs.toArray(new Object[stmtArgs.size()]));
        writer.nextControlFlow("else");
        writer.emitStatement("throw new IllegalStateException(\"Required Argument with key '%s' was not found for '%s'. "
                + "If this field is not required add '@NotRequired' annotation\")", field.getKey(), field.getName());
        writer.endControlFlow();

    }

    private void writeOptionalInjectArgumentField(JavaWriter writer, BundleFieldBinding field) throws IOException {
        List<String> stmtArgs = new ArrayList<String>();
        writer.beginControlFlow("if (bundle.containsKey(%s))", StringLiteral.forValue(field.getKey()).toString());
        String stmt = "target.".concat(field.getName()).concat(" = ");
        if (field.needsToBeCast()) {
            stmt = stmt.concat("(%s) ");
            stmtArgs.add(field.getType());
        }
        stmt = stmt.concat("bundle.get%s(%s");
        stmtArgs.add(field.getBundleType());
        stmtArgs.add(StringLiteral.forValue(field.getKey()).toString());
        if (field.canHaveDefault()) {
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
