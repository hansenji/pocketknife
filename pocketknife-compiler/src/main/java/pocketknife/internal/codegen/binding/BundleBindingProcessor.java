package pocketknife.internal.codegen.binding;

import android.os.Build;
import pocketknife.BindArgument;
import pocketknife.BundleSerializer;
import pocketknife.NotRequired;
import pocketknife.PocketKnifeBundleSerializer;
import pocketknife.SaveState;
import pocketknife.internal.codegen.BundleFieldBinding;
import pocketknife.internal.codegen.InvalidTypeException;
import pocketknife.internal.codegen.KeySpec;

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

public class BundleBindingProcessor extends BindingProcessor {

    public BundleBindingProcessor(Messager messager, Elements elements, Types types) {
        super(messager, elements, types);
    }

    public Map<TypeElement, BundleBindingAdapterGenerator> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, BundleBindingAdapterGenerator> targetClassMap = new LinkedHashMap<TypeElement, BundleBindingAdapterGenerator>();
        Set<String> erasedTargetNames = new LinkedHashSet<String>(); // used for parent lookup.

        // Process each @SaveState
        for (Element element : env.getElementsAnnotatedWith(SaveState.class)) {
            SaveState annotation = element.getAnnotation(SaveState.class);
            if (annotation == null) {
                continue;
            }
            try {
                parseSaveState(element, targetClassMap, erasedTargetNames);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                error(element, "Unable to generate bundle adapter for @SaveState.\n\n%s", stackTrace);
            }
        }

        // Process each @BindArgument Annotation
        for (Element element : env.getElementsAnnotatedWith(BindArgument.class)) {
            BindArgument annotation = element.getAnnotation(BindArgument.class);
            if (annotation == null) {
                continue;
            }
            try {
                parseBindAnnotation(element, targetClassMap, erasedTargetNames);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                error(element, "Unable to generate bundle adapter for @BindAnnotation.\n\n%s", stackTrace);
            }
        }

        // Try to find a parent adapter for each adapter
        for (Map.Entry<TypeElement, BundleBindingAdapterGenerator> entry : targetClassMap.entrySet()) {
            TypeElement parentElement = findParent(entry.getKey(), erasedTargetNames);
            if (parentElement != null) {
                entry.getValue().setParentAdapter(getPackageName(parentElement), parentElement.getSimpleName() + BUNDLE_ADAPTER_SUFFIX);
            }
        }

        return targetClassMap;
    }

    private void parseSaveState(Element element, Map<TypeElement, BundleBindingAdapterGenerator> targetClassMap, Set<String> erasedTargetNames)
            throws ClassNotFoundException, InvalidTypeException {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target has all the appropriate information for type
        TypeMirror type = element.asType();
        if (type instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) type;
            type = typeVariable.getUpperBound();
        }

        validateNotRequiredArguments(element);
        validateForCodeGeneration(SaveState.class, element);
        validateBindingPackage(SaveState.class, element);

        TypeMirror bundleSerializer = getAnnotationElementClass(element, BundleSerializer.class);
        validateSerializer(element, BundleSerializer.class, bundleSerializer, PocketKnifeBundleSerializer.class);

        // Assemble information on the bind point.
        String name = element.getSimpleName().toString();
        String bundleType = null;
        NotRequired notRequired = element.getAnnotation(NotRequired.class);
        boolean required = notRequired == null;
        int minSdk = Build.VERSION_CODES.FROYO;
        if (!required) {
            minSdk = notRequired.value();
        }
        boolean canHaveDefault = false;
        boolean needsToBeCast = false;
        if (bundleSerializer == null) {
            bundleType = typeUtil.getBundleType(type);
            canHaveDefault = !required && canHaveDefault(type, minSdk);
            needsToBeCast = typeUtil.needToCastBundleType(type);
        }



        BundleBindingAdapterGenerator bundleBindingAdapterGenerator = getOrCreateTargetClass(targetClassMap, enclosingElement);
        BundleFieldBinding binding = new BundleFieldBinding(SAVE_STATE, name, type, bundleType, new KeySpec(null, generateKey(SAVE_STATE_KEY_PREFIX, name)),
                needsToBeCast, canHaveDefault, required, bundleSerializer);
        bundleBindingAdapterGenerator.addField(binding);

        // Add the type-erased version to the valid targets set.
        erasedTargetNames.add(enclosingElement.toString());
    }

    private void parseBindAnnotation(Element element, Map<TypeElement, BundleBindingAdapterGenerator> targetClassMap, Set<String> erasedTargetNames)
            throws InvalidTypeException {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target has all the appropriate information for type
        TypeMirror type = element.asType();
        if (element instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) type;
            type = typeVariable.getUpperBound();
        }


        validateNotRequiredArguments(element);
        validateForCodeGeneration(BindArgument.class, element);
        validateBindingPackage(BindArgument.class, element);

        TypeMirror bundleSerializer = getAnnotationElementClass(element, BundleSerializer.class);
        validateSerializer(element, BundleSerializer.class, bundleSerializer, PocketKnifeBundleSerializer.class);

        // Assemble information on the bind point
        String name = element.getSimpleName().toString();
        String bundleType = null;
        KeySpec key = getKey(element);
        boolean canHaveDefault = false;
        boolean needsToBeCast = false;
        NotRequired notRequired = element.getAnnotation(NotRequired.class);
        boolean required = notRequired == null;
        int minSdk = Build.VERSION_CODES.FROYO;
        if (!required) {
            minSdk = notRequired.value();
        }
        if (bundleSerializer == null) {
            bundleType = typeUtil.getBundleType(type);
            canHaveDefault = !required && canHaveDefault(type, minSdk);
            needsToBeCast = typeUtil.needToCastBundleType(type);
        }

        BundleBindingAdapterGenerator bundleBindingAdapterGenerator = getOrCreateTargetClass(targetClassMap, enclosingElement);
        BundleFieldBinding binding = new BundleFieldBinding(BundleFieldBinding.AnnotationType.ARGUMENT, name, type, bundleType, key,
                needsToBeCast, canHaveDefault, required, bundleSerializer);
        bundleBindingAdapterGenerator.orRequired(required);
        bundleBindingAdapterGenerator.addField(binding);

        // Add the type-erased version to the valid targets set.
        erasedTargetNames.add(enclosingElement.toString());
    }

    private KeySpec getKey(Element element) {
        if (isDefaultAnnotationElement(element, BindArgument.class.getName(), "value")) {
            return new KeySpec(null, generateKey(BundleFieldBinding.ARGUMENT_KEY_PREFIX, element.getSimpleName().toString()));
        }
        return new KeySpec(null, element.getAnnotation(BindArgument.class).value());
    }

    private boolean canHaveDefault(TypeMirror type, int minSdk) {
        return typeUtil.isPrimitive(type) || minSdk >= Build.VERSION_CODES.HONEYCOMB_MR1 && types.isAssignable(type, typeUtil.charSequenceType);
    }

    private BundleBindingAdapterGenerator getOrCreateTargetClass(Map<TypeElement, BundleBindingAdapterGenerator> targetClassMap,
                                                                   TypeElement enclosingElement) {
        BundleBindingAdapterGenerator bundleBindingAdapterGenerator = targetClassMap.get(enclosingElement);
        if (bundleBindingAdapterGenerator == null) {
            TypeMirror targetType = enclosingElement.asType();
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage) + BUNDLE_ADAPTER_SUFFIX;

            bundleBindingAdapterGenerator = new BundleBindingAdapterGenerator(classPackage, className, targetType, typeUtil);
            targetClassMap.put(enclosingElement, bundleBindingAdapterGenerator);
        }
        return bundleBindingAdapterGenerator;
    }

}
