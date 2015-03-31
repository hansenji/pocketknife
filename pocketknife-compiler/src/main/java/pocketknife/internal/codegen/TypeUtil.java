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
    public static final String BUNDLE = "android.os.Bundle";
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

    public String getBundleType(TypeMirror type) throws InvalidTypeException {
        // Primitive
        if (isPrimitive(type)) {
            return getPrimitiveType(type);
        }

        // Array
        if (isArrayType(type)) {
            return getArrayType((ArrayType) type);
        }

        // ArrayList
        if (isArrayListType(type)) {
            return getArrayListType((DeclaredType) type);
        }

        // Sparse ParcelableArray
        if (isSparseParcelableArray(type)) {
            return "SparseParcelableArray";
        }

        // Other types
        if (types.isAssignable(type, bundleType)) {
            return "Bundle";
        }

        if (isAggregateType(type)) {
            return getAggregateType(type);
        }

        if (types.isAssignable(type, serializableType)) {
            return "Serializable";
        }
        throw new InvalidTypeException(InvalidTypeException.Container.BUNDLE, type);
    }

    public String getIntentType(TypeMirror type) throws InvalidTypeException {
        // Primitive
        if (isPrimitive(type)) {
            return getPrimitiveType(type);
        }

        // Array
        if (isArrayType(type)) {
            return getArrayType((ArrayType) type);
        }

        // ArrayList
        if (isArrayListType(type)) {
            return getArrayListType((DeclaredType) type);
        }

        if (types.isAssignable(type, bundleType)) {
            return "Bundle";
        }

        if (isAggregateType(type)) {
            return getAggregateType(type);
        }

        if (types.isAssignable(type, serializableType)) {
            return "Serializable";
        }
        throw new InvalidTypeException(InvalidTypeException.Container.INTENT, type);
    }

    public boolean isPrimitive(TypeMirror type) {
        return type.getKind().isPrimitive();
    }

    private boolean isAggregateType(TypeMirror type) {
        return types.isAssignable(type, stringType) || types.isAssignable(type, charSequenceType) || types.isAssignable(type, parcelableType);
    }

    private boolean isArrayType(TypeMirror type) {
        if (TypeKind.ARRAY == type.getKind() && type instanceof ArrayType) {
            TypeMirror componentType = ((ArrayType) type).getComponentType();
            return isPrimitive(componentType) || isAggregateType(componentType);
        }
        return false;
    }

    private boolean isArrayListType(TypeMirror type) {
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

    private boolean isSparseParcelableArray(TypeMirror type) {
        if (types.isAssignable(types.erasure(type), sparseArrayType) && type instanceof DeclaredType) {
            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
            if (typeArguments.size() == 1) {
                return types.isAssignable(typeArguments.get(0), parcelableType);
            }
        }
        return false;
    }

    private String getPrimitiveType(TypeMirror type) throws InvalidTypeException {
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
                throw new InvalidTypeException(type);
        }
    }

    private String getArrayType(ArrayType type) throws InvalidTypeException {
        TypeMirror componentType = type.getComponentType();
        if (isPrimitive(componentType)) {
            try {
                return getPrimitiveType(componentType).concat("Array");
            } catch (InvalidTypeException e) {
                throw new InvalidTypeException(type);
            }
        }
        try {
            return getAggregateType(componentType).concat("Array");
        } catch (InvalidTypeException e) {
            throw new InvalidTypeException(type);
        }
    }

    private String getArrayListType(DeclaredType type) throws InvalidTypeException {
        TypeMirror arg = type.getTypeArguments().get(0);
        if (types.isAssignable(arg, integerType)) {
            return "IntegerArrayList";
        }
        try {
            return getAggregateType(arg).concat("ArrayList");
        } catch (InvalidTypeException e) {
            throw new InvalidTypeException(type);
        }
    }

    private String getAggregateType(TypeMirror type) throws InvalidTypeException {
        if (types.isAssignable(type, stringType)) { // String is subtype of CharSequence should go first
            return "String";
        }
        if (types.isAssignable(type, charSequenceType)) {
            return "CharSequence";
        }
        if (types.isAssignable(type, parcelableType)) {
            return "Parcelable";
        }
        throw new InvalidTypeException(type);
    }

    public boolean needToCastBundleType(TypeMirror type) throws InvalidTypeException {
        if (isPrimitive(type)) {
            return false;
        }

        if (isArrayType(type)) {
            return needToCastArrayType((ArrayType) type);
        }

        if (isArrayListType(type)) {
            return false;
        }

        if (isSparseParcelableArray(type)) {
            return needToCastSparseParcelableArray((DeclaredType) type);
        }

        if (types.isAssignable(type, bundleType)) {
            return !types.isSameType(type, bundleType);
        }

        if (isAggregateType(type)) {
            return needToCastAggregateType(type);
        }

        if (types.isAssignable(type, serializableType)) {
            return !types.isSameType(type, serializableType);
        }

        throw new InvalidTypeException(InvalidTypeException.Container.BUNDLE, type);
    }

    public boolean needToCastIntentType(TypeMirror type) throws InvalidTypeException {
        if (isPrimitive(type)) {
            return false;
        }

        if (isArrayType(type)) {
            return needToCastArrayType((ArrayType) type);
        }

        if (isArrayListType(type)) {
            return false;
        }

        if (types.isAssignable(type, bundleType)) {
            return !types.isSameType(type, bundleType);
        }

        if (isAggregateType(type)) {
            return needToCastAggregateType(type);
        }

        if (types.isAssignable(type, serializableType)) {
            return !types.isSameType(type, serializableType);
        }

        throw new InvalidTypeException(InvalidTypeException.Container.INTENT, type);
    }

    private boolean needToCastArrayType(ArrayType type) throws InvalidTypeException {
        TypeMirror componentType = type.getComponentType();
        if (isPrimitive(componentType)) {
            return false;
        }
        try {
            return needToCastAggregateType(componentType);
        } catch (InvalidTypeException e) {
            throw new InvalidTypeException(type);
        }
    }

    private boolean needToCastAggregateType(TypeMirror type) throws InvalidTypeException {
        if (types.isAssignable(type, charSequenceType)) {
            return !types.isSameType(type, charSequenceType);
        }
        if (types.isAssignable(type, stringType)) {
            return !types.isSameType(type, stringType);
        }
        if (types.isAssignable(type, parcelableType)) {
            return !types.isSameType(type, parcelableType);
        }
        throw new InvalidTypeException(type);
    }

    private boolean needToCastSparseParcelableArray(DeclaredType type) {
        List<? extends TypeMirror> typeArguments = type.getTypeArguments();
        return !types.isAssignable(typeArguments.get(0), parcelableType);
    }
}
