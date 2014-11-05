package pocketknife.internal.codegen;

import android.os.Build;
import pocketknife.InjectExtra;
import pocketknife.NotRequired;
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

public class IntentProcessor extends PKProcessor {

    public IntentProcessor(Messager messager, Elements elements, Types types) {
        super(messager, elements, types);
    }

    public Map<TypeElement, IntentAdapterGenerator> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, IntentAdapterGenerator> targetClassMap = new LinkedHashMap<TypeElement, IntentAdapterGenerator>();

        // Process each @InjectExtra
        for (Element element : env.getElementsAnnotatedWith(InjectExtra.class)) {
            try {
                parseInjectExtra(element, targetClassMap);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                error(element, "Unable to generate intent adapter for @InjectExtra.\n\n%s", stackTrace);
            }
        }

        return targetClassMap;
    }

    private void parseInjectExtra(Element element, Map<TypeElement, IntentAdapterGenerator> targetClassMap) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target has all the appropriate information for type
        TypeMirror elementType = element.asType();
        if (elementType instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) elementType;
            elementType = typeVariable.getUpperBound();
        }

        String intentType = getIntentType(element, elementType);
        Boolean needsToBeCast = needsToBeCast(element, elementType);

        boolean hasError = !areInjectExtraArgumentsValid(element);
        hasError |= intentType == null;
        hasError |= needsToBeCast == null;
        hasError |= !isValidForGeneratedCode(InjectExtra.class, "fields", element);
        hasError |= isBindingInWrongPackage(InjectExtra.class, element);

        if (hasError) {
            return;
        }

        // Assemble information on the injection point
        String name = element.getSimpleName().toString();
        InjectExtra annotation = element.getAnnotation(InjectExtra.class);
        String key = annotation.value();
        NotRequired notRequired = element.getAnnotation(NotRequired.class);
        boolean required = notRequired == null;
        boolean hasDefault = hasDefault(elementType);

        IntentAdapterGenerator intentAdapterGenerator = getOrCreateTargetClass(targetClassMap, enclosingElement);
        IntentFieldBinding binding = new IntentFieldBinding(name, elementType.toString(), intentType, key, needsToBeCast, hasDefault, required);
        intentAdapterGenerator.addField(binding);
    }

    private String getIntentType(Element element, TypeMirror type) {
        try {
            // Primitive
            if (isPrimitive(type)) {
                return getPrimitiveIntentType(type);
            }

            // Array
            if (TypeKind.ARRAY == type.getKind() && type instanceof ArrayType) {
                String componentType = getArrayComponentIntentType(((ArrayType) type).getComponentType());
                if (componentType != null && !componentType.isEmpty()) {
                    return componentType.concat("Array");
                }
            }

            // Check ArrayList
            if (types.isAssignable(types.erasure(type), typeUtil.arrayListType)) {
                String arrayListType = getArrayListIntentType(type);
                if (arrayListType != null && !arrayListType.isEmpty()) {
                    return arrayListType;
                }
            }

            // Other Types
            if (types.isAssignable(type, typeUtil.bundleType)) {
                return "Bundle";
            }

            String aggregateIntentType = getAggregateIntentType(type);
            if (aggregateIntentType != null && !aggregateIntentType.isEmpty()) {
                return aggregateIntentType;
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

    private String getArrayComponentIntentType(TypeMirror type) throws InvalidTypeException {
        if (isPrimitive(type)) {
            try {
                return getPrimitiveIntentType(type);
            } catch (InvalidTypeException e) {
                throw new InvalidTypeException("Array", type);
            }
        }
        return getAggregateIntentType(type);
    }

    private String getArrayListIntentType(TypeMirror type) {
        if (type instanceof DeclaredType) {
            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
            if (typeArguments.size() == 1) {
                String componentType = getArrayListComponentIntentType(typeArguments.get(0));
                if (componentType != null && !componentType.isEmpty()) {
                    return componentType.concat("ArrayList");
                }
            }
        }
        return null;
    }

    private String getAggregateIntentType(TypeMirror type) {
        if (types.isAssignable(type, typeUtil.stringType)) { // String is subtype of CharSequence should go first
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

    private String getArrayListComponentIntentType(TypeMirror type) {
        if (types.isAssignable(type, typeUtil.integerType)) {
            return "Integer";
        }
        return getAggregateIntentType(type);
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
            Boolean result = needToCastArrayList(type);
            if (result != null) {
                return result;
            }
        }

        // Other
        if (types.isAssignable(type, typeUtil.bundleType)) {
            return !types.isSameType(type, typeUtil.bundleType);
        }

        Boolean result = needToCastAggregateIntentType(type);
        if (result != null) {
            return result;
        }


        if (types.isAssignable(type, typeUtil.serializableType)) {
            return !types.isSameType(type, typeUtil.serializableType);
        }

        error(element, "Error invalid intent type '%s'", type);
        return null;
    }

    private Boolean needToCastArrayComponentType(TypeMirror type) {
        if (isPrimitive(type)) {
            return false;
        }
        return needToCastAggregateIntentType(type);
    }

    private Boolean needToCastArrayList(TypeMirror type) {
        if (type instanceof DeclaredType) {
            List<? extends TypeMirror> typeArguments = ((DeclaredType) type).getTypeArguments();
            if (typeArguments.size() == 1) {
                TypeMirror argType = typeArguments.get(0);
                if (types.isAssignable(argType, typeUtil.parcelableType)) {
                    return false;
                }
                if (types.isAssignable(argType, typeUtil.integerType)) {
                    return !types.isSameType(argType, typeUtil.integerType);
                }
                Boolean result = needToCastAggregateIntentType(argType);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private Boolean needToCastAggregateIntentType(TypeMirror type) {
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

    private boolean areInjectExtraArgumentsValid(Element element) {
        NotRequired notRequired = element.getAnnotation(NotRequired.class);
        if (notRequired != null && notRequired.value() < Build.VERSION_CODES.FROYO) {
            error(element, "NotRequired value must be FROYO(8)+");
            return false;
        }
        InjectExtra injectExtra = element.getAnnotation(InjectExtra.class);
        if (injectExtra.value() == null || injectExtra.value().trim().isEmpty()) {
            error(element, "InjectAnnotation value must not be empty");
            return false;
        }
        return true;
    }

    private boolean hasDefault(TypeMirror type) {
        return isPrimitive(type);
    }

    private boolean isPrimitive(TypeMirror type) {
        return type.getKind().isPrimitive();
    }

    private IntentAdapterGenerator getOrCreateTargetClass(Map<TypeElement, IntentAdapterGenerator> targetClassMap, TypeElement enclosingElement) {
        IntentAdapterGenerator intentAdapterGenerator = targetClassMap.get(enclosingElement);
        if (intentAdapterGenerator == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage) + GeneratedAdapters.INTENT_ADAPTER_SUFFIX;

            intentAdapterGenerator = new IntentAdapterGenerator(classPackage, className, targetType);
            targetClassMap.put(enclosingElement, intentAdapterGenerator);
        }
        return intentAdapterGenerator;
    }
}
