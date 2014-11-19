package pocketknife.internal.codegen;

import android.os.Build;
import pocketknife.InjectArgument;
import pocketknife.NotRequired;
import pocketknife.SaveState;
import pocketknife.internal.GeneratedAdapters;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BundleProcessor extends PKProcessor {

    public BundleProcessor(Messager messager, Elements elements, Types types) {
        super(messager, elements, types);
    }

    public Map<TypeElement, BundleAdapterGenerator> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, BundleAdapterGenerator> targetClassMap = new LinkedHashMap<TypeElement, BundleAdapterGenerator>();

        // Process each @SaveState
        for (Element element : env.getElementsAnnotatedWith(SaveState.class)) {
            try {
                parseSaveState(element, targetClassMap);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                error(element, "Unable to generate bundle adapter for @SaveState.\n\n%s", stackTrace);
            }
        }

        // Process each @InjectAnnotation
        for (Element element : env.getElementsAnnotatedWith(InjectArgument.class)) {
            try {
                parseInjectAnnotation(element, targetClassMap);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                error(element, "Unable to generate bundle adapter for @InjectAnnotation.\n\n%s", stackTrace);
            }
        }

        return targetClassMap;
    }

    private void parseSaveState(Element element, Map<TypeElement, BundleAdapterGenerator> targetClassMap)
            throws ClassNotFoundException {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target has all the appropriate information for type
        TypeMirror elementType = element.asType();
        if (elementType instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) elementType;
            elementType = typeVariable.getUpperBound();
        }

        String bundleType = getBundleType(element, elementType);
        Boolean needsToBeCast = needsToBeCast(element, elementType);

        boolean hasError = !areSaveStateArgumentsValid(element);
        hasError |= bundleType == null;
        hasError |= needsToBeCast == null;
        hasError |= !isValidForGeneratedCode(SaveState.class, "fields", element);
        hasError |= isBindingInWrongPackage(SaveState.class, element);

        if (hasError) {
            return;
        }

        // Assemble information on the injection point.
        String name = element.getSimpleName().toString();
        NotRequired notRequired = element.getAnnotation(NotRequired.class);
        boolean required = notRequired == null;
        int minSdk = Build.VERSION_CODES.FROYO;
        if (!required) {
            minSdk = notRequired.value();
        }
        boolean canHaveDefault = !required && canHaveDefault(elementType, minSdk);

        BundleAdapterGenerator bundleAdapterGenerator = getOrCreateTargetClass(targetClassMap, enclosingElement);
        BundleFieldBinding binding = new BundleFieldBinding(name, elementType.toString(), bundleType, needsToBeCast, canHaveDefault, required);
        bundleAdapterGenerator.addField(binding);
    }

    private void parseInjectAnnotation(Element element, Map<TypeElement, BundleAdapterGenerator> targetClassMap) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target has all the appropriate information for type
        TypeMirror elementType = element.asType();
        if (element instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) elementType;
            elementType = typeVariable.getUpperBound();
        }

        String bundleType = getBundleType(element, elementType);
        Boolean needsToBeCast = needsToBeCast(element, elementType);

        boolean hasError = !areInjectArgumentArgumentsValid(element);
        hasError |= bundleType == null;
        hasError |= needsToBeCast == null;
        hasError |= !isValidForGeneratedCode(InjectArgument.class, "fields", element);
        hasError |= isBindingInWrongPackage(InjectArgument.class, element);

        if (hasError) {
            return;
        }

        // Assemble information on the injection point
        String name = element.getSimpleName().toString();
        InjectArgument annotation = element.getAnnotation(InjectArgument.class);
        String key = annotation.value();
        NotRequired notRequired = element.getAnnotation(NotRequired.class);
        boolean required = notRequired == null;
        int minSdk = Build.VERSION_CODES.FROYO;
        if (!required) {
            minSdk = notRequired.value();
        }
        boolean canHaveDefault = !required && canHaveDefault(elementType, minSdk);

        BundleAdapterGenerator bundleAdapterGenerator = getOrCreateTargetClass(targetClassMap, enclosingElement);
        BundleFieldBinding binding = new BundleFieldBinding(name, elementType.toString(), bundleType, key, needsToBeCast, canHaveDefault, required);
        bundleAdapterGenerator.orRequired(required);
        bundleAdapterGenerator.addField(binding);
    }

    private boolean areSaveStateArgumentsValid(Element element) {
        NotRequired notRequired = element.getAnnotation(NotRequired.class);
        if (notRequired != null && notRequired.value() < Build.VERSION_CODES.FROYO) {
            error(element, "NotRequired value must be FROYO(8)+");
            return false;
        }
        return true;
    }

    private boolean areInjectArgumentArgumentsValid(Element element) {
        NotRequired notRequired = element.getAnnotation(NotRequired.class);
        if (notRequired != null && notRequired.value() < Build.VERSION_CODES.FROYO) {
            error(element, "NotRequired value must be FROYO(8)+");
            return false;
        }
        InjectArgument injectArgument = element.getAnnotation(InjectArgument.class);
        if (injectArgument.value() == null || injectArgument.value().trim().isEmpty()) {
            error(element, "InjectAnnotation value must not be empty");
            return false;
        }
        return true;
    }

    private boolean canHaveDefault(TypeMirror type, int minSdk) {
        return isPrimitive(type) || minSdk >= Build.VERSION_CODES.HONEYCOMB_MR1 && types.isAssignable(type, typeUtil.charSequenceType);
    }

    private String getBundleType(Element element, TypeMirror type) {
        try {
            // Check Primitive
            if (isPrimitive(type)) {
                return getPrimitiveBundleType(type);
            }

            // Check Array
            if (TypeKind.ARRAY == type.getKind() && type instanceof ArrayType) {
                String componentType = getArrayComponentBundleType(((ArrayType) type).getComponentType());
                if (componentType != null && !componentType.isEmpty()) {
                    return componentType.concat("Array");
                }
            }

            // Check ArrayList
            if (types.isAssignable(types.erasure(type), typeUtil.arrayListType)) {
                String arrayListType = getArrayListBundleType(type);
                if (arrayListType != null && !arrayListType.isEmpty()) {
                    return arrayListType;
                }
            }

            // Check Sparse Parcelable Array
            if (isSparesParcelableArray(type)) {
                return "SparseParcelableArray";
            }

            // Other Types
            if (types.isAssignable(type, typeUtil.bundleType)) {
                return "Bundle";
            }

            String aggregateBundleType = getAggregateBundleType(type);
            if (aggregateBundleType != null && !aggregateBundleType.isEmpty()) {
                return aggregateBundleType;
            }

            if (types.isAssignable(type, typeUtil.serializableType)) {
                return "Serializable";
            }

        } catch (InvalidTypeException e) {
            error(element, "%s", e.getMessage());
            return null;
        }

        error(element, "Invalid bundle type '%s'", type.toString());
        return null;
    }

    private boolean isPrimitive(TypeMirror type) {
        return type.getKind().isPrimitive();
    }

    private String getPrimitiveBundleType(TypeMirror type) throws InvalidTypeException {
        // No unboxing due to the nullable nature of Boxed primitives.
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

    private String getArrayComponentBundleType(TypeMirror type) throws InvalidTypeException {
        if (isPrimitive(type)) {
            try {
                return getPrimitiveBundleType(type);
            } catch (InvalidTypeException e) {
                throw new InvalidTypeException("Array", type);
            }
        }
        return getAggregateBundleType(type);
    }

    private String getArrayListBundleType(TypeMirror type) {
        if (type instanceof DeclaredType) {
            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
            if (typeArguments.size() == 1) {
                String arrayListComponentType = getArrayListComponentBundleType(typeArguments.get(0));
                if (arrayListComponentType != null && !arrayListComponentType.isEmpty()) {
                    return arrayListComponentType.concat("ArrayList");
                }
            }
        }
        return null;
    }

    private String getArrayListComponentBundleType(TypeMirror type) {
        if (types.isAssignable(type, typeUtil.integerType)) {
            return "Integer";
        }
        return getAggregateBundleType(type);
    }

    private String getAggregateBundleType(TypeMirror type) {
        if (types.isAssignable(type, typeUtil.stringType)) { // String is subtype of charsequence should go first.
            return "String";
        }
        if (types.isAssignable(type, typeUtil.charSequenceType)) {
            return "CharSequence";
        }
        if (types.isAssignable(type, typeUtil.parcelableType)) {
            return "Parcelable";
        }
        return null;
    }

    private boolean isSparesParcelableArray(TypeMirror type) {
        if (types.isAssignable(types.erasure(type), typeUtil.sparseArrayType) && type instanceof DeclaredType) {
            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
            if (typeArguments.size() == 1) {
                return types.isAssignable(typeArguments.get(0), typeUtil.parcelableType);
            }
        }
        return false;
    }

    private Boolean needsToBeCast(Element element, TypeMirror type) {
        if (isPrimitive(type)) {
            return false;
        }

        // Check Array
        if (TypeKind.ARRAY == type.getKind() && type instanceof ArrayType) {
            Boolean result = needToCastArrayComponentType(((ArrayType) type).getComponentType());
            if (result != null) {
                return result;
            }
        }

        // ArrayList
        if (types.isAssignable(types.erasure(type), typeUtil.arrayListType)) {
            return false;
        }

        // Sparse Parcelable Array
        if (types.isAssignable(types.erasure(type), typeUtil.sparseArrayType)) {
            Boolean result = needToCastSparseParcelableArray(type);
            if (result != null) {
                return result;
            }
        }

        // Other types
        if (types.isAssignable(type, typeUtil.bundleType)) {
            return !types.isSameType(type, typeUtil.bundleType);
        }

        Boolean result = needToCastAggregateBundleType(type);
        if (result != null) {
            return result;
        }


        if (types.isAssignable(type, typeUtil.serializableType)) {
            return !types.isSameType(type, typeUtil.serializableType);
        }

        error(element, "Error invalid bundle type '%s'", type);
        return null;
    }

    private Boolean needToCastArrayComponentType(TypeMirror type) {
        if (isPrimitive(type)) {
            return false;
        }
        return needToCastAggregateBundleType(type);
    }

    private Boolean needToCastAggregateBundleType(TypeMirror type) {
        if (types.isAssignable(type, typeUtil.charSequenceType)) {
            return !types.isSameType(type, typeUtil.charSequenceType);
        }
        if (types.isAssignable(type, typeUtil.stringType)) {
            return !types.isSameType(type, typeUtil.stringType);
        }
        if (types.isAssignable(type, typeUtil.parcelableType)) {
            return !types.isSameType(type, typeUtil.parcelableType);
        }
        return null;
    }

    private Boolean needToCastSparseParcelableArray(TypeMirror type) {
        if (type instanceof DeclaredType) {
            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
            if (typeArguments.size() == 1 && types.isAssignable(typeArguments.get(0), typeUtil.parcelableType)) {
                return false;
            }
        }
        return null;
    }

    private BundleAdapterGenerator getOrCreateTargetClass(Map<TypeElement, BundleAdapterGenerator> targetClassMap, TypeElement enclosingElement) {
        BundleAdapterGenerator bundleAdapterGenerator = targetClassMap.get(enclosingElement);
        if (bundleAdapterGenerator == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage) + GeneratedAdapters.BUNDLE_ADAPTER_SUFFIX;

            bundleAdapterGenerator = new BundleAdapterGenerator(classPackage, className, targetType);
            targetClassMap.put(enclosingElement, bundleAdapterGenerator);
        }
        return bundleAdapterGenerator;
    }

}
