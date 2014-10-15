package pocketknife.internal.codegen;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class TypeUtil {
    private static final CharSequence SERIALIZABLE = "java.io.Serializable";
    private static final CharSequence PARCELABLE = "android.os.Parcelable";
    //    private static final CharSequence BINDER = "android.os.IBinder"; // Api 18+
    private static final CharSequence BUNDLE = "android.os.Bundle";
    private static final CharSequence STRING = "java.lang.String";
    private static final CharSequence CHAR_SEQUENCE = "java.lang.CharSequence";
    private static final CharSequence INTEGER = "java.lang.Integer";
    private static final CharSequence ARRAY_LIST = "java.util.ArrayList";
    private static final CharSequence SPARSE_ARRAY = "android.util.SparseArray";

    public final TypeMirror serializableType;
    public final TypeMirror parcelableType;
    //    public final TypeMirror binderType; // API 18+
    public final TypeMirror bundleType;
    public final TypeMirror stringType;
    public final TypeMirror charSequenceType;
    public final TypeMirror integerType;
    public final TypeMirror arrayListType;
    public final TypeMirror sparseArrayType;

    private static TypeUtil instance;

    public static synchronized TypeUtil getInstance(Elements elements, Types types) {
        if (instance == null) {
            instance = new TypeUtil(elements, types);
        }
        return instance;
    }

    public TypeUtil(Elements elements, Types types) {
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
    }
}
