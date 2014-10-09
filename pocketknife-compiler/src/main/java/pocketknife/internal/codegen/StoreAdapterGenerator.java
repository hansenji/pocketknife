package pocketknife.internal.codegen;

import com.squareup.javawriter.JavaWriter;
import pocketknife.internal.GeneratedAdapters;
import pocketknife.internal.StoreBinding;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.EnumSet;
import java.util.List;
import java.util.ArrayList;


import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

final class StoreAdapterGenerator {

    private final Set<StoreFieldBinding> fields = new LinkedHashSet<StoreFieldBinding>();
    private final String classPackage;
    private final String className;
    private final String targetType;
    private final Elements elements;
    private final Types types;

    public StoreAdapterGenerator(String classPackage, String className, String targetType, Elements elements, Types types) {
        this.classPackage = classPackage;
        this.className = className;
        this.targetType = targetType;
        this.elements = elements;
        this.types = types;
    }

    public void addField(StoreFieldBinding binding) {
        fields.add(binding);
    }

    public void generate(JavaWriter writer) throws IOException, IllegalBundleTypeException {
        writer.emitSingleLineComment(AdapterJavadoc.GENERATED_BY_POCKETKNIFE);
        writer.emitPackage(classPackage);
        writer.emitImports("android.os.Bundle");
        writer.emitEmptyLine();
        writer.beginType(className, "class", EnumSet.of(PUBLIC, FINAL), JavaWriter.type(StoreBinding.class, targetType));
        writer.emitEmptyLine();
        writeBundleKeys(writer);
        // write Save State
        writeSaveState(writer);
        writer.emitEmptyLine();
        // Write restoreState
        writeRestoreState(writer);
        writer.endType();



    }

    private void writeBundleKeys(JavaWriter writer) throws IOException {
        writer.emitSingleLineComment(AdapterJavadoc.BUNDLE_KEYS);
        for (StoreFieldBinding field : fields) {
            writer.emitField(String.class.getCanonicalName(), field.getKey(), EnumSet.of(PRIVATE, STATIC, FINAL), JavaWriter.stringLiteral(field.getKey()));
        }
    }

    private void writeSaveState(JavaWriter writer) throws IOException, IllegalBundleTypeException {
        writer.emitJavadoc(AdapterJavadoc.SAVE_METHOD, targetType);
        writer.beginMethod("void", GeneratedAdapters.SAVE_METHOD, EnumSet.of(PUBLIC), targetType, "target", "Bundle", "bundle");
        for (StoreFieldBinding field : fields) {
            writeSaveFieldState(writer, field);
        }
        writer.endMethod();
    }

    private void writeSaveFieldState(JavaWriter writer, StoreFieldBinding field) throws IOException, IllegalBundleTypeException {
        writer.emitSingleLineComment(field.getDescription());
        writer.emitStatement("bundle.put%s(%s, target.%s)", field.getBundleType(elements, types), field.getKey(), field.getName());
    }

    private void writeRestoreState(JavaWriter writer) throws IOException, IllegalBundleTypeException {
        writer.emitJavadoc(AdapterJavadoc.RESTORE_METHOD, targetType);
        writer.beginMethod("void", GeneratedAdapters.RESTORE_METHOD, EnumSet.of(PUBLIC), targetType, "target", "Bundle", "bundle");
        writer.beginControlFlow("if (bundle != null)");
        for (StoreFieldBinding field : fields) {
            writeRestoreFieldState(writer, field);
        }
        writer.endControlFlow();
        writer.endMethod();
    }

    private void writeRestoreFieldState(JavaWriter writer, StoreFieldBinding field) throws IOException, IllegalBundleTypeException {
        writer.emitSingleLineComment(field.getDescription());
        List<String> stmtArgs = new ArrayList<String>();
        String stmt = "target.".concat(field.getName()).concat(" = ");
        if (field.needsToBeCast(elements, types)) { //TODO double check casting
            stmt = stmt.concat("(%s) ");
            stmtArgs.add(field.getType());
        }
        stmt = stmt.concat("bundle.get%s(%s");
        stmtArgs.add(field.getBundleType(elements, types));
        stmtArgs.add(field.getKey());
        if (field.hasDefault(types)) {
            stmt = stmt.concat(", %s");
            stmtArgs.add(field.getDefaultValue());
        }
        stmt = stmt.concat(")");
        writer.emitStatement(stmt, stmtArgs.toArray(new Object[stmtArgs.size()]));
    }

    public CharSequence getFqcn() {
        return classPackage + "." + className;
    }
}
