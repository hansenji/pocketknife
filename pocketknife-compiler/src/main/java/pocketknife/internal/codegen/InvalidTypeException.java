package pocketknife.internal.codegen;

import javax.lang.model.type.TypeMirror;

public class InvalidTypeException extends Exception {
    public InvalidTypeException(String parameter, TypeMirror type) {
        super(String.format("Invalid type '%s' for '%s'", parameter, type.toString()));
    }
}
