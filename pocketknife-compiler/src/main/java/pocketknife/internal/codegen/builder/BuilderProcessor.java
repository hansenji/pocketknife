package pocketknife.internal.codegen.builder;

import pocketknife.internal.codegen.BaseProcessor;
import pocketknife.internal.codegen.TypeUtil;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;

import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.tools.Diagnostic.Kind.ERROR;
import static pocketknife.internal.GeneratedAdapters.ANDROID_PREFIX;
import static pocketknife.internal.GeneratedAdapters.JAVA_PREFIX;

public abstract class BuilderProcessor extends BaseProcessor {

    protected static final String GENERATOR_PREFIX = "PocketKnife_";

    protected final Messager messager;
    protected final Elements elements;
    protected final Types types;
    protected final TypeUtil typeUtil;

    public BuilderProcessor(Messager messager, Elements elements, Types types) {
        this.messager = messager;
        this.elements = elements;
        this.types = types;
        this.typeUtil = TypeUtil.getInstance(elements, types);
    }

    protected void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        messager.printMessage(ERROR, message, element);
    }

    protected void validateEnclosingClass(Class<? extends Annotation> annotationClass, TypeElement enclosingElement) {
        if (enclosingElement.getKind() != INTERFACE) {
            throw new IllegalStateException(String.format("@%s must be in an interface", annotationClass.getSimpleName()));
        }
        if (enclosingElement.getModifiers().contains(PRIVATE)) {
            throw new IllegalStateException(String.format("@%s may not be contained in private interface", annotationClass.getSimpleName()));
        }
    }

    protected void validateBindingPackage(Class<? extends Annotation> annotationClass, Element element) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        String qualifiedName = enclosingElement.getQualifiedName().toString();

        if (qualifiedName.startsWith(ANDROID_PREFIX)) {
            throw new IllegalStateException(String.format("@%s-annotated interface incorrectly in Android framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName));
        }
        if (qualifiedName.startsWith(JAVA_PREFIX)) {
            throw new IllegalStateException(String.format("@%s-annotated interface incorrectly in Java framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName));
        }
    }

    protected String getPackageName(TypeElement type) {
        return elements.getPackageOf(type).getQualifiedName().toString();
    }

    protected String getClassName(TypeElement typeElement, String packageName) {
        int packageLen = packageName.length() + 1;
        return typeElement.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }
}
