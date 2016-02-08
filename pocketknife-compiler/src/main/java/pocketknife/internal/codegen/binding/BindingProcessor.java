package pocketknife.internal.codegen.binding;

import android.os.Build;
import com.google.common.base.CaseFormat;
import pocketknife.NotRequired;
import pocketknife.internal.codegen.Access;
import pocketknife.internal.codegen.BaseProcessor;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.Set;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;
import static pocketknife.internal.GeneratedAdapters.ANDROID_PREFIX;
import static pocketknife.internal.GeneratedAdapters.JAVA_PREFIX;

public abstract class BindingProcessor extends BaseProcessor {

    protected Messager messager;

    private static final String IS = "is";
    private static final String GET = "get";
    private static final String SET = "set";
    private static final String[] GETTER_PREFIXES = {IS, GET};

    public BindingProcessor(Messager messager, Elements elements, Types types) {
        super(elements, types);
        this.messager = messager;

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

    protected void validateNotRequiredArguments(Element element) {
        NotRequired notRequired = element.getAnnotation(NotRequired.class);
        if (notRequired != null && notRequired.value() < Build.VERSION_CODES.FROYO) {
            throw new IllegalStateException("NotRequired value must be FROYO(8)+");
        }
    }

    protected void validateForCodeGeneration(Class<? extends Annotation> annotationClass, Element element) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify method modifiers
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(STATIC)) {
            throw new IllegalStateException(String.format("@%s fields must not be static. (%s.%s)",
                    annotationClass.getSimpleName(), enclosingElement.getQualifiedName(),
                    element.getSimpleName()));
        }

        // Verify Containing type.
        if (enclosingElement.getKind() != CLASS) {
            throw new IllegalStateException(String.format("@%s fields may only be contained in classes. (%s.%s)",
                    annotationClass.getSimpleName(), enclosingElement.getQualifiedName(),
                    element.getSimpleName()));
        }

        // Verify containing class visibility is not private
        if (enclosingElement.getModifiers().contains(PRIVATE)) {
            throw new IllegalStateException(String.format("@%s fields may not be contained in private classes (%s.%s)", annotationClass.getSimpleName(),
                    enclosingElement.getQualifiedName(), element.getSimpleName()));
        }

    }

    protected Access getAccess(Class<? extends Annotation> annotationClass, Element element, TypeElement enclosingElement) {
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(PROTECTED) || modifiers.contains(PRIVATE)) {
            String getter = findGetter(element);
            String setter = findSetter(element);
            if (getter == null || setter == null) {
                throw new IllegalStateException(String.format("@%s fields must have a Java Bean getter and a setter if it is private or protected. (%s.%s)",
                        annotationClass.getSimpleName(), enclosingElement.getQualifiedName(),
                        element.getSimpleName()));
            }
            return new Access(Access.Type.METHOD, getter, setter);
        }
        String name = element.getSimpleName().toString();
        return new Access(Access.Type.FIELD, name, name);

    }

    private String findGetter(Element element) {
        String field = element.getSimpleName().toString();
        Element parent = element.getEnclosingElement();
        for (Element child : parent.getEnclosedElements()) {
            if (child.getKind() == ElementKind.METHOD && child instanceof ExecutableElement && ((ExecutableElement) child).getParameters().isEmpty()) {
                String name = child.getSimpleName().toString();
                for (String prefix : GETTER_PREFIXES) {
                    if (name.startsWith(prefix) && name.substring(prefix.length()).equals(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, field))) {
                        return name;
                    }
                }
            }
        }
        return null;
    }

    private String findSetter(Element element) {
        String field = element.getSimpleName().toString();
        Element parent = element.getEnclosingElement();
        for (Element child : parent.getEnclosedElements()) {
            if (child.getKind() == METHOD && child instanceof ExecutableElement && ((ExecutableElement) child).getParameters().size() == 1 && !(
                    (ExecutableElement) child).isVarArgs()) {
                String name = child.getSimpleName().toString();
                if (name.startsWith(SET) && name.substring(SET.length()).equals(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, field))) {
                    return name;
                }
            }
        }
        return null;
    }

    protected void validateBindingPackage(Class<? extends Annotation> annotationClass, Element element) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        String qualifiedName = enclosingElement.getQualifiedName().toString();

        if (qualifiedName.startsWith(ANDROID_PREFIX)) {
            throw new IllegalStateException(String.format("@%s-annotated class incorrectly in Android framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName));
        }
        if (qualifiedName.startsWith(JAVA_PREFIX)) {
            throw new IllegalStateException(String.format("@%s-annotated class incorrectly in Java framework package. (%s",
                    annotationClass.getSimpleName(), qualifiedName));
        }
    }

    protected TypeElement findParent(TypeElement typeElement, Set<String> parents) {
        TypeMirror type;
        while (true) {
            type = typeElement.getSuperclass();
            if (type.getKind() == TypeKind.NONE) {
                return null;
            }
            typeElement = (TypeElement) ((DeclaredType) type).asElement();
            if (parents.contains(typeElement.toString())) {
                return typeElement;
            }
        }
    }

}
