package pocketknife.internal.codegen;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import java.lang.annotation.Annotation;
import java.util.Set;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;
import static pocketknife.internal.GeneratedAdapters.ANDROID_PREFIX;
import static pocketknife.internal.GeneratedAdapters.JAVA_PREFIX;

public abstract class PKProcessor {

    protected Messager messager;
    protected Elements elements;
    protected Types types;
    protected TypeUtil typeUtil;

    public PKProcessor(Messager messager, Elements elements, Types types) {
        this.messager = messager;
        this.elements = elements;
        this.types = types;
        this.typeUtil = TypeUtil.getInstance(elements);
    }

    protected String getPackageName(TypeElement type) {
        return elements.getPackageOf(type).getQualifiedName().toString();
    }

    protected String getClassName(TypeElement typeElement, String packageName) {
        int packageLen = packageName.length() + 1;
        return typeElement.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    protected void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        messager.printMessage(ERROR, message, element);
    }

    protected boolean isValidForGeneratedCode(Class<? extends Annotation> annotationClass, String targetThing, Element element) {
        boolean isValid = true;
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify method modifiers
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
            error(element, "@%s %s must not be private, protected, or static. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            isValid = false;
        }

        // Verify Containing type.
        if (enclosingElement.getKind() != CLASS) {
            error(enclosingElement, "@%s %s may only be contained in classes. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            isValid = false;
        }

        // Verify containing class visibility is not private
        if (enclosingElement.getModifiers().contains(PRIVATE)) {
            error(enclosingElement, "@%s %s may not be contained in private classes (%s.%s)", annotationClass.getSimpleName(), targetThing,
                    enclosingElement.getQualifiedName(), element.getSimpleName());
            isValid = false;
        }

        return isValid;
    }

    protected boolean isBindingInWrongPackage(Class<? extends Annotation> annotationClass, Element element) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        String qualifiedName = enclosingElement.getQualifiedName().toString();

        if (qualifiedName.startsWith(ANDROID_PREFIX)) {
            error(element, "@%s-annotated class incorrectly in Android framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return true;
        }
        if (qualifiedName.startsWith(JAVA_PREFIX)) {
            error(element, "@%s-annotated class incorrectly in Java framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return true;
        }

        return false;
    }

}
