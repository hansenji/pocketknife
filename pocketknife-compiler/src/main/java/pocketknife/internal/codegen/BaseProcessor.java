package pocketknife.internal.codegen;

import com.google.common.base.CaseFormat;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.util.Map;

public abstract class BaseProcessor {

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
}
