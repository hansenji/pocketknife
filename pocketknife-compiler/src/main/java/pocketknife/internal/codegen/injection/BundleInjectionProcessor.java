package pocketknife.internal.codegen.injection;

import android.os.Build;
import pocketknife.InjectArgument;
import pocketknife.NotRequired;
import pocketknife.SaveState;
import pocketknife.internal.codegen.InvalidTypeException;
import pocketknife.internal.codegen.BundleFieldBinding;

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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static pocketknife.internal.GeneratedAdapters.BUNDLE_ADAPTER_SUFFIX;
import static pocketknife.internal.codegen.BundleFieldBinding.AnnotationType.SAVE_STATE;
import static pocketknife.internal.codegen.BundleFieldBinding.SAVE_STATE_KEY_PREFIX;

public class BundleInjectionProcessor extends InjectionProcessor {

    public BundleInjectionProcessor(Messager messager, Elements elements, Types types) {
        super(messager, elements, types);
    }

    public Map<TypeElement, BundleInjectionAdapterGenerator> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, BundleInjectionAdapterGenerator> targetClassMap = new LinkedHashMap<TypeElement, BundleInjectionAdapterGenerator>();
        Set<String> erasedTargetNames = new LinkedHashSet<String>(); // used for parent lookup.

        // Process each @SaveState
        for (Element element : env.getElementsAnnotatedWith(SaveState.class)) {
            try {
                parseSaveState(element, targetClassMap, erasedTargetNames);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                error(element, "Unable to generate bundle adapter for @SaveState.\n\n%s", stackTrace);
            }
        }

        // Process each @InjectAnnotation
        for (Element element : env.getElementsAnnotatedWith(InjectArgument.class)) {
            try {
                parseInjectAnnotation(element, targetClassMap, erasedTargetNames);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                error(element, "Unable to generate bundle adapter for @InjectAnnotation.\n\n%s", stackTrace);
            }
        }

        // Try to find a parent adapter for each adapter
        for (Map.Entry<TypeElement, BundleInjectionAdapterGenerator> entry : targetClassMap.entrySet()) {
            String parentClassFqcn = findParentFqcn(entry.getKey(), erasedTargetNames);
            if (parentClassFqcn != null) {
                entry.getValue().setParentAdapter(parentClassFqcn + BUNDLE_ADAPTER_SUFFIX);
            }
        }

        return targetClassMap;
    }

    private void parseSaveState(Element element, Map<TypeElement, BundleInjectionAdapterGenerator> targetClassMap, Set<String> erasedTargetNames)
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

        BundleInjectionAdapterGenerator bundleInjectionAdapterGenerator = getOrCreateTargetClass(targetClassMap, enclosingElement);
        BundleFieldBinding binding = new BundleFieldBinding(SAVE_STATE, name, elementType.toString(), bundleType, generateKey(SAVE_STATE_KEY_PREFIX, name),
                needsToBeCast, canHaveDefault, required);
        bundleInjectionAdapterGenerator.addField(binding);

        // Add the type-erased version to the valid targets set.
        erasedTargetNames.add(enclosingElement.toString());
    }

    private void parseInjectAnnotation(Element element, Map<TypeElement, BundleInjectionAdapterGenerator> targetClassMap, Set<String> erasedTargetNames) {
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
        String key = getKey(element);
        NotRequired notRequired = element.getAnnotation(NotRequired.class);
        boolean required = notRequired == null;
        int minSdk = Build.VERSION_CODES.FROYO;
        if (!required) {
            minSdk = notRequired.value();
        }
        boolean canHaveDefault = !required && canHaveDefault(elementType, minSdk);

        BundleInjectionAdapterGenerator bundleInjectionAdapterGenerator = getOrCreateTargetClass(targetClassMap, enclosingElement);
        BundleFieldBinding binding = new BundleFieldBinding(BundleFieldBinding.AnnotationType.ARGUMENT, name, elementType.toString(), bundleType, key,
                needsToBeCast, canHaveDefault, required);
        bundleInjectionAdapterGenerator.orRequired(required);
        bundleInjectionAdapterGenerator.addField(binding);

        // Add the type-erased version to the valid targets set.
        erasedTargetNames.add(enclosingElement.toString());
    }

    private String getKey(Element element) {
        if (isDefaultAnnotationElement(element, InjectArgument.class.getName(), "value")) {
            return generateKey(BundleFieldBinding.ARGUMENT_KEY_PREFIX, element.getSimpleName().toString());
        }
        return element.getAnnotation(InjectArgument.class).value();
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
            if (isSparseParcelableArray(type)) {
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

    private boolean isSparseParcelableArray(TypeMirror type) {
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

    private BundleInjectionAdapterGenerator getOrCreateTargetClass(Map<TypeElement, BundleInjectionAdapterGenerator> targetClassMap,
                                                                   TypeElement enclosingElement) {
        BundleInjectionAdapterGenerator bundleInjectionAdapterGenerator = targetClassMap.get(enclosingElement);
        if (bundleInjectionAdapterGenerator == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage) + BUNDLE_ADAPTER_SUFFIX;

            bundleInjectionAdapterGenerator = new BundleInjectionAdapterGenerator(classPackage, className, targetType);
            targetClassMap.put(enclosingElement, bundleInjectionAdapterGenerator);
        }
        return bundleInjectionAdapterGenerator;
    }

}
