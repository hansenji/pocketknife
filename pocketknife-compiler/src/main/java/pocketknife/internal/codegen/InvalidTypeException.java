package pocketknife.internal.codegen;

import javax.lang.model.type.TypeMirror;

public class InvalidTypeException extends Exception {

    public enum Container { BUNDLE, INTENT }

    public InvalidTypeException(TypeMirror type) {
        super(String.format("Invalid type %s", type.toString()));
    }

    public InvalidTypeException(Container container, TypeMirror type) {
        super(String.format("Invalid type %s for %s", type.toString(), container.toString()));
    }
}
