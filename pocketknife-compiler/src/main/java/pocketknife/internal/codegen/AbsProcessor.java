package pocketknife.internal.codegen;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.tools.Diagnostic.Kind.ERROR;

public abstract class AbsProcessor {

    protected Messager messager;
    protected Elements elements;
    protected Types types;
    protected TypeUtil typeUtil;

    public AbsProcessor(Messager messager, Elements elements, Types types) {
        this.messager = messager;
        this.elements = elements;
        this.types = types;
        this.typeUtil = TypeUtil.getInstance(elements, types);
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
}
