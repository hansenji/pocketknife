package pocketknife.internal.codegen;

import javax.lang.model.element.Element;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;

public class TypeUtil {
    private static final String SERIALIZABLE = "java.io.Serializable";
    private static final String PARCELABLE = "android.os.Parcelable";
    //    private static final CharSequence BINDER = "android.os.IBinder"; // Api 18+
    private static final String BUNDLE = "android.os.Bundle";
    private static final String STRING = "java.lang.String";
    private static final String CHAR_SEQUENCE = "java.lang.CharSequence";
    private static final String INTEGER = "java.lang.Integer";
    private static final String ARRAY_LIST = "java.util.ArrayList";
    private static final String SPARSE_ARRAY = "android.util.SparseArray";

    public static final String INTENT = "android.content.Intent";
    public static final String CONTEXT = "android.content.Context";
    public static final String BUILD = "android.os.Build";
    public static final String URI = "android.net.Uri";


    public final TypeMirror serializableType;
    public final TypeMirror parcelableType;
    //    public final TypeMirror binderType; // API 18+
    public final TypeMirror bundleType;
    public final TypeMirror stringType;
    public final TypeMirror charSequenceType;
    public final TypeMirror integerType;
    public final TypeMirror arrayListType;
    public final TypeMirror sparseArrayType;
    public final TypeMirror intentType;

    private static TypeUtil instance;

    private final Types types;

    public static synchronized TypeUtil getInstance(Elements elements, Types types) {
        if (instance == null) {
            instance = new TypeUtil(elements, types);
        }
        return instance;
    }

    private TypeUtil(Elements elements, Types types) {
        this.types = types;
        Element element = elements.getTypeElement(SERIALIZABLE);
        if (element == null) {
            throw new IllegalStateException("Unable to find Serializable type");
        }
        serializableType = element.asType();
        element = elements.getTypeElement(PARCELABLE);
        if (element == null) {
            throw new IllegalStateException("Unable to find Parcelable type");
        }
        parcelableType = element.asType();
//        element = elements.getTypeElement(BINDER);
//        if (element == null) {
//            throw new IllegalStateException("Unable to find Binder type");
//        }
//        binderType = element.asType();
        element = elements.getTypeElement(BUNDLE);
        if (element == null) {
            throw new IllegalStateException("Unable to find Bundle type");
        }
        bundleType = element.asType();
        element = elements.getTypeElement(STRING);
        if (element == null) {
            throw new IllegalStateException("Unable to find String type");
        }
        stringType = element.asType();
        element = elements.getTypeElement(CHAR_SEQUENCE);
        if (element == null) {
            throw new IllegalStateException("Unable to find CharSequence type");
        }
        charSequenceType = element.asType();
        element = elements.getTypeElement(INTEGER);
        if (element == null) {
            throw new IllegalStateException("Unable to find Integer type");
        }
        integerType = element.asType();
        element = elements.getTypeElement(ARRAY_LIST);
        if (element == null) {
            throw new IllegalStateException("Unable to find ArrayList type");
        }
        arrayListType = element.asType();
        element = elements.getTypeElement(SPARSE_ARRAY);
        if (element == null) {
            throw new IllegalStateException("Unable to find SparseArray type");
        }
        sparseArrayType = element.asType();
        element = elements.getTypeElement(INTENT);
        if (element == null) {
            throw new IllegalStateException("Unable to find Intent type");
        }
        intentType = element.asType();
    }

    public String getIntentType(TypeMirror type) throws InvalidTypeException {
        // Primitive
        if (isPrimitive(type)) {
            return getPrimitiveIntentType(type);
        }

        // Array
        if (isIntentArrayType(type)) {
            return getIntentArrayType((ArrayType) type);
        }

        // ArrayList
        if (isIntentArrayListType(type)) {
            return getIntentArrayListType((DeclaredType) type);
        }

        if (types.isAssignable(type, bundleType)) {
            return "Bundle";
        }

        if (isAggregateType(type)) {
            return getAggregateIntentType(type);
        }

        if (types.isAssignable(type, serializableType)) {
            return "Serializable";
        }
        throw new InvalidTypeException(InvalidTypeException.Container.INTENT, type);
    }

    private String getIntentArrayListType(DeclaredType type) throws InvalidTypeException {
        TypeMirror arg = type.getTypeArguments().get(0);
        if (types.isAssignable(arg, integerType)) {
            return "IntegerArrayList";
        }
        try {
            return getAggregateIntentType(arg).concat("ArrayList");
        } catch (InvalidTypeException e) {
            throw new InvalidTypeException("ArrayList", type);
        }
    }

    private boolean isPrimitive(TypeMirror type) {
        return type.getKind().isPrimitive();
    }

    private boolean isAggregateType(TypeMirror type) {
        return types.isAssignable(type, stringType) || types.isAssignable(type, charSequenceType) || types.isAssignable(type, parcelableType);
    }

    private boolean isIntentArrayType(TypeMirror type) {
        if (TypeKind.ARRAY == type.getKind() && type instanceof ArrayType) {
            TypeMirror componentType = ((ArrayType) type).getComponentType();
            return isPrimitive(componentType) || isAggregateType(componentType);
        }
        return false;
    }

    private boolean isIntentArrayListType(TypeMirror type) {
        if (types.isAssignable(types.erasure(type), arrayListType)) {
            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
            if (typeArguments.size() == 1) {
                TypeMirror arg = typeArguments.get(0);
                if (types.isAssignable(arg, integerType)) {
                    return true;
                }
                if (isAggregateType(arg)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getIntentArrayType(ArrayType type) throws InvalidTypeException {
        TypeMirror componentType = type.getComponentType();
        if (isPrimitive(componentType)) {
            try {
                return getPrimitiveIntentType(componentType);
            } catch (InvalidTypeException e) {
                throw new InvalidTypeException("Array", type);
            }
        }
        try {
            return getAggregateIntentType(componentType);
        } catch (InvalidTypeException e) {
            throw new InvalidTypeException("Array", type);
        }
    }

    private String getAggregateIntentType(TypeMirror type) throws InvalidTypeException {
        if (types.isAssignable(type, stringType)) { // String is subtype of CharSequence should go first
            return "String";
        }
        if (types.isAssignable(type, charSequenceType)) {
            return "CharSequence";
        }
        if (types.isAssignable(type, parcelableType)) {
            return "Parcelable";
        }
        throw new InvalidTypeException(InvalidTypeException.Container.INTENT, type);
    }

    private String getPrimitiveIntentType(TypeMirror type) throws InvalidTypeException {
        // No unboxing due to the nullable nature of boxed primitives
        switch (type.getKind()) {
            case BOOLEAN:
                return "Boolean";
            case BYTE:
                return "Byte";
            case SHORT:
                return "Short";
            case INT:
                return "Int";
            case LONG:
                return "Long";
            case CHAR:
                return "Char";
            case FLOAT:
                return "Float";
            case DOUBLE:
                return "Double";
            default:
                throw new InvalidTypeException("Primitive", type);
        }
    }
}
