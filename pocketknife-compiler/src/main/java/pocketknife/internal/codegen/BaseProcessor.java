package pocketknife.internal.codegen;

import com.google.common.base.CaseFormat;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.Map;

public abstract class BaseProcessor {

    protected Elements elements;
    protected Types types;
    protected TypeUtil typeUtil;

    public BaseProcessor(Elements elements, Types types) {
        this.elements = elements;
        this.types = types;
        this.typeUtil = TypeUtil.getInstance(elements, types);
    }

    protected boolean isDefaultAnnotationElement(Element element, String annotation, String annotationElement) {
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            if (annotation.equals(mirror.getAnnotationType().toString())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet()) {
                    if (annotationElement.equals(entry.getKey().getSimpleName().toString())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected String generateKey(String prefix, String name) {
        return prefix + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }

    protected TypeMirror getAnnotationElementClass(Element element, Class<? extends Annotation> annotation) {
        return getAnnotationElementClass(element, annotation, "value");
    }

    protected TypeMirror getAnnotationElementClass(Element element, Class<? extends Annotation> annotation, String annotationElement) {
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            if (annotation.getName().equals(mirror.getAnnotationType().toString())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet()) {
                    if (annotationElement.equals(entry.getKey().getSimpleName().toString())) {
                        return (TypeMirror) entry.getValue().getValue();
                    }
                }
                break;
            }
        }
        return null;
    }

    protected void validateSerializer(Element element, Class<? extends Annotation> annotation, TypeMirror serializer, Class abstractSerializer) {
        if (serializer == null) {
            return;
        }
        Element absSerializerElement = elements.getTypeElement(abstractSerializer.getName());
        if (absSerializerElement == null) {
            throw new IllegalStateException(String.format("Unable to find %s type", abstractSerializer.getName()));
        }
        TypeMirror absSerializerMirror = absSerializerElement.asType();
        if (!types.isAssignable(serializer, types.erasure(absSerializerMirror))) {
            throw new IllegalStateException(String.format("@%s value must extend %s", annotation.getName(), abstractSerializer.getName()));
        }
        TypeMirror elementType = element.asType();
        for (TypeMirror superType : types.directSupertypes(serializer)) {
            if (types.isAssignable(superType, types.erasure(absSerializerMirror)) &&  superType instanceof DeclaredType) {
                if (types.isSameType(((DeclaredType) superType).getTypeArguments().get(0), elementType)) {
                    return;
                } else {
                    throw new IllegalStateException(String.format("Serializer T must be of type %s", elementType.toString()));
                }
            }
        }
        throw new IllegalStateException(String.format("%s must extend %s", serializer.toString(), absSerializerMirror.toString()));
    }

}
