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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
            throws ClassNotFoundException, InvalidTypeException {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target has all the appropriate information for type
        TypeMirror elementType = element.asType();
        if (elementType instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) elementType;
            elementType = typeVariable.getUpperBound();
        }

        validateNotRequiredArguments(element);
        validateForCodeGeneration(SaveState.class, element);
        validateBindingPackage(SaveState.class, element);

        // Assemble information on the injection point.
        String name = element.getSimpleName().toString();
        String bundleType = typeUtil.getBundleType(elementType);
        NotRequired notRequired = element.getAnnotation(NotRequired.class);
        boolean required = notRequired == null;
        int minSdk = Build.VERSION_CODES.FROYO;
        if (!required) {
            minSdk = notRequired.value();
        }
        boolean canHaveDefault = !required && canHaveDefault(elementType, minSdk);
        boolean needsToBeCast = typeUtil.needToCastBundleType(elementType);

        BundleInjectionAdapterGenerator bundleInjectionAdapterGenerator = getOrCreateTargetClass(targetClassMap, enclosingElement);
        BundleFieldBinding binding = new BundleFieldBinding(SAVE_STATE, name, elementType.toString(), bundleType, generateKey(SAVE_STATE_KEY_PREFIX, name),
                needsToBeCast, canHaveDefault, required);
        bundleInjectionAdapterGenerator.addField(binding);

        // Add the type-erased version to the valid targets set.
        erasedTargetNames.add(enclosingElement.toString());
    }

    private void parseInjectAnnotation(Element element, Map<TypeElement, BundleInjectionAdapterGenerator> targetClassMap, Set<String> erasedTargetNames)
            throws InvalidTypeException {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target has all the appropriate information for type
        TypeMirror elementType = element.asType();
        if (element instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) elementType;
            elementType = typeVariable.getUpperBound();
        }

        validateNotRequiredArguments(element);
        validateForCodeGeneration(InjectArgument.class, element);
        validateBindingPackage(InjectArgument.class, element);

        // Assemble information on the injection point
        String name = element.getSimpleName().toString();
        String bundleType = typeUtil.getBundleType(elementType);
        String key = getKey(element);
        NotRequired notRequired = element.getAnnotation(NotRequired.class);
        boolean required = notRequired == null;
        int minSdk = Build.VERSION_CODES.FROYO;
        if (!required) {
            minSdk = notRequired.value();
        }
        boolean canHaveDefault = !required && canHaveDefault(elementType, minSdk);
        boolean needsToBeCast = typeUtil.needToCastBundleType(elementType);

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

    private boolean canHaveDefault(TypeMirror type, int minSdk) {
        return typeUtil.isPrimitive(type) || minSdk >= Build.VERSION_CODES.HONEYCOMB_MR1 && types.isAssignable(type, typeUtil.charSequenceType);
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
