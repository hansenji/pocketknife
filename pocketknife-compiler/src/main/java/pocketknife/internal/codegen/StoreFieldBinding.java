package pocketknife.internal.codegen;

import android.os.Build;
import com.google.common.base.CaseFormat;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;

import static pocketknife.internal.GeneratedAdapters.STORE_KEY_PREFIX;

/**
 * Created by hansenji on 9/16/14.
 */
public class StoreFieldBinding implements Binding {

    private static final CharSequence SERIALIZABLE =  "java.io.Serializable";
    private static final CharSequence PARCELABLE = "android.os.Parcelable";
    private static final CharSequence BINDER = "android.os.IBinder";
    private static final CharSequence BUNDLE = "android.os.Bundle";
    private static final CharSequence STRING = "java.lang.String";
    private static final CharSequence CHAR_SEQUENCE = "java.lang.CharSequence";
    private static final CharSequence INTEGER = "java.lang.Integer";
    private static final CharSequence ARRAY_LIST = "java.util.ArrayList";
    private static final CharSequence SPARSE_ARRAY = "android.util.SparseArray";


    private static TypeMirror serializableType;
    private static TypeMirror parcelableType;
    private static TypeMirror binderType;
    private static TypeMirror bundleType;
    private static TypeMirror stringType;
    private static TypeMirror charSequenceType;
    private static TypeMirror integerType;
    private static TypeMirror arrayListType;
    private static TypeMirror sparseArrayType;

    private final String name;
    private final TypeMirror type;
    private final String defaultValue;
    private final int minSdk;

    public StoreFieldBinding(String name, TypeMirror type, String defaultValue, int minSdk) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.minSdk = minSdk;
    }

    @Override
    public String getDescription() {
        return "Field '" + getType() + " " + name + "'";
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type.toString();
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StoreFieldBinding && name.equals(((StoreFieldBinding) obj).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public boolean hasDefault(Types types) {
        boolean canHaveDefault = false;
        if (isPrimitive(types, type)) {
            canHaveDefault = true;
        }
        if (minSdk >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            canHaveDefault = types.isAssignable(type, charSequenceType); // String is of type CharSequence
        }

        return canHaveDefault && !defaultValue.isEmpty();
    }

    private boolean isPrimitive(Types types, TypeMirror type) {
        return type.getKind().isPrimitive();
    }

    public String getBundleType(Elements elements, Types types) throws IllegalBundleTypeException {
        setupComparableTypes(elements);
        // Check Primitive
        if (isPrimitive(types, type)) {
            return getPrimitiveBundleType(type);
        }
        // Check Array
        if (TypeKind.ARRAY == type.getKind() && type instanceof ArrayType) {
            String componentType = getArrayBundleType(types, ((ArrayType) type).getComponentType());
            if (componentType != null) {
                return componentType.concat("Array");
            }
        }

        // Check ArrayList
        if (types.isAssignable(types.erasure(type), arrayListType)) {
            String arrayListType = getArrayListBundleType(types);
            if (arrayListType != null) {
                return arrayListType;
            }
        }

        // Sparse Parcelable Array
        if (isSparseParcelableArray(types)) {
            return "SparseParcelableArray";
        }

        // Other types
        String aggregateBundleType = getAggregateBundleType(types, type);
        if (aggregateBundleType != null) {
            return aggregateBundleType;
        }

        if (types.isAssignable(type, binderType)) {
            return "Binder";
        }
        if (types.isAssignable(type, bundleType)) {
            return "Bundle";
        }
        if (types.isAssignable(type, serializableType)) {
            return "Serializable";
        }

        throw new IllegalBundleTypeException(this);
    }

    private boolean isSparseParcelableArray(Types types) {
        if (types.isAssignable(types.erasure(type), sparseArrayType) && type instanceof DeclaredType) {
            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
            if (typeArguments.size() == 1) {
                return types.isAssignable(typeArguments.get(0), parcelableType);
            }
        }
        return false;
    }

    private String getArrayListBundleType(Types types) {
        if (type instanceof DeclaredType) {
            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
            if (typeArguments.size() == 1) {
                String arrayListItemBundleType = getArrayListItemBundleType(types, typeArguments.get(0));
                if (arrayListItemBundleType != null) {
                    return arrayListItemBundleType.concat("ArrayList");
                }
            }
        }
        return null;
    }

    private String getArrayListItemBundleType(Types types, TypeMirror type) {
        String aggregateBundleType = getAggregateBundleType(types, type);
        if (aggregateBundleType != null) {
            return aggregateBundleType;
        }
        if (types.isAssignable(type, integerType)) {
            return "Integer";
        }
        return null;
    }

    private String getAggregateBundleType(Types types, TypeMirror type) {
        if (types.isAssignable(type, charSequenceType)) {
            return "CharSequence";
        }
        if (types.isAssignable(type, stringType)) {
            return "String";
        }
        if (types.isAssignable(type, parcelableType)) {
            return "Parcelable";
        }
        return null;
    }

    private String getArrayBundleType(Types types, TypeMirror type) throws IllegalBundleTypeException {
        if (isPrimitive(types, type)) {
            return getPrimitiveBundleType(type);
        }
        return getAggregateBundleType(types, type);
    }

    private String getPrimitiveBundleType(TypeMirror type) throws IllegalBundleTypeException {
        // TODO Talk to Jeff about unboxing
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
                throw new IllegalBundleTypeException(this);
        }
    }

    public String getKey() {
        return STORE_KEY_PREFIX + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, getName());
    }

    public boolean needsToBeCast(Elements elements, Types types) throws IllegalBundleTypeException {
        setupComparableTypes(elements);
        // Check Primitive
        if (isPrimitive(types, type)) {
            return false;
        }

        // Check Array
        if (TypeKind.ARRAY == type.getKind() && type instanceof ArrayType) {
            Boolean result = needToCastArrayType(types, ((ArrayType) type).getComponentType());
            if (result != null) {
                return result;
            }
        }

        // ArrayList
        if (types.isAssignable(types.erasure(type), arrayListType)) {
            Boolean result = needToCastArrayList(types);
            if (result != null) {
                return result;
            }
        }

        // Sparse Parcelable Array
        if (types.isAssignable(types.erasure(type), sparseArrayType)) {
            Boolean result = needToCastSparseParcelableArray(types);
            if (result != null) {
                return result;
            }
        }

        // Other types
        Boolean result = needToCastAggregateBundleType(types, type);
        if (result != null) {
            return result;
        }

        if (types.isAssignable(type, binderType)) {
            return !types.isSameType(type, binderType);
        }

        if (types.isAssignable(type, bundleType)) {
            return !types.isSameType(type, bundleType);
        }

        if (types.isAssignable(type, serializableType)) {
            return !types.isSameType(type, serializableType);
        }

        throw new IllegalBundleTypeException(this);
    }

    private Boolean needToCastSparseParcelableArray(Types types) {
        if (type instanceof DeclaredType) {
            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
            if (typeArguments.size() == 1 && types.isAssignable(typeArguments.get(0), parcelableType)) {
                return false;
            }
        }
        return null;
    }

    private Boolean needToCastArrayType(Types types, TypeMirror type) {
        if (isPrimitive(types, type)) {
            return false;
        }
        return needToCastAggregateBundleType(types, type);
    }

    private Boolean needToCastArrayList(Types types) {
        if (type instanceof DeclaredType) {
            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
            if (typeArguments.size() == 1) {
                Boolean result = needToCastAggregateBundleType(types, type);
                if (result != null) {
                    return result;
                }
                if (types.isAssignable(type, stringType)) {
                    return !types.isSameType(type, stringType);
                }
            }
        }
        return null;
    }

    private Boolean needToCastAggregateBundleType(Types types, TypeMirror type) {
        if (types.isAssignable(type, charSequenceType)) {
            return !types.isSameType(type, charSequenceType);
        }
        if (types.isAssignable(type, stringType)) {
            return !types.isSameType(type, stringType);
        }
        if (types.isAssignable(type, parcelableType)) {
            return !types.isSameType(type, parcelableType);
        }
        return null;
    }

    private static void setupComparableTypes(Elements elements) {
        if (serializableType == null) {
            Element element = elements.getTypeElement(SERIALIZABLE);
            if (element == null) {
                throw new IllegalStateException("Unable to find Serializable type");
            }
            serializableType = element.asType();
        }
        if (parcelableType == null) {
            Element element = elements.getTypeElement(PARCELABLE);
            if (element == null) {
                throw new IllegalStateException("Unable to find Parcelable type");
            }
            parcelableType = element.asType();
        }
        if (binderType == null) {
            Element element = elements.getTypeElement(BINDER);
            if (element == null) {
                throw new IllegalStateException("Unable to find Binder type");
            }
            binderType = element.asType();
        }
        if (bundleType == null) {
            Element element = elements.getTypeElement(BUNDLE);
            if (element == null) {
                throw new IllegalStateException("Unable to find Bundle type");
            }
            bundleType = element.asType();
        }
        if (stringType == null) {
            Element element = elements.getTypeElement(STRING);
            if (element == null) {
                throw new IllegalStateException("Unable to find String type");
            }
            stringType = element.asType();
        }
        if (charSequenceType == null) {
            Element element = elements.getTypeElement(CHAR_SEQUENCE);
            if (element == null) {
                throw new IllegalStateException("Unable to find CharSequence type");
            }
            charSequenceType = element.asType();
        }
        if (integerType == null) {
            Element element = elements.getTypeElement(INTEGER);
            if (element == null) {
                throw new IllegalStateException("Unable to find Integer type");
            }
            integerType = element.asType();
        }
        if (arrayListType == null) {
            Element element = elements.getTypeElement(ARRAY_LIST);
            if (element == null) {
                throw new IllegalStateException("Unable to find ArrayList type");
            }
            arrayListType = element.asType();
        }
        if (sparseArrayType == null) {
            Element element = elements.getTypeElement(SPARSE_ARRAY);
            if (element == null) {
                throw new IllegalStateException("Unable to find SparseArray type");
            }
            sparseArrayType = element.asType();
        }
    }
}
