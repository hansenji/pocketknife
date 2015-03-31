package pocketknife.internal.codegen.injection;

import pocketknife.InjectExtra;
import pocketknife.NotRequired;
import pocketknife.internal.codegen.IntentFieldBinding;
import pocketknife.internal.codegen.InvalidTypeException;

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

import static pocketknife.internal.GeneratedAdapters.INTENT_ADAPTER_SUFFIX;

public class IntentInjectionProcessor extends InjectionProcessor {

    public IntentInjectionProcessor(Messager messager, Elements elements, Types types) {
        super(messager, elements, types);
    }

    public Map<TypeElement, IntentInjectionAdapterGenerator> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, IntentInjectionAdapterGenerator> targetClassMap = new LinkedHashMap<TypeElement, IntentInjectionAdapterGenerator>();
        Set<String> erasedTargetNames = new LinkedHashSet<String>();

        // Process each @InjectExtra
        for (Element element : env.getElementsAnnotatedWith(InjectExtra.class)) {
            try {
                parseInjectExtra(element, targetClassMap, erasedTargetNames);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                error(element, "Unable to generate intent adapter for @InjectExtra.\n\n%s", stackTrace);
            }
        }

        // Try to find a parent adapter for each adapter
        for (Map.Entry<TypeElement, IntentInjectionAdapterGenerator> entry : targetClassMap.entrySet()) {
            String parentClassFqcn = findParentFqcn(entry.getKey(), erasedTargetNames);
            if (parentClassFqcn != null) {
                entry.getValue().setParentAdapter(parentClassFqcn + INTENT_ADAPTER_SUFFIX);
            }
        }

        return targetClassMap;
    }

    private void parseInjectExtra(Element element, Map<TypeElement, IntentInjectionAdapterGenerator> targetClassMap, Set<String> erasedTargetNames)
            throws InvalidTypeException {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target has all the appropriate information for type
        TypeMirror elementType = element.asType();
        if (elementType instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) elementType;
            elementType = typeVariable.getUpperBound();
        }

        validateNotRequiredArguments(element);
        validateForCodeGeneration(InjectExtra.class, element);
        validateBindingPackage(InjectExtra.class, element);

        // Assemble information on the injection point
        String name = element.getSimpleName().toString();
        String intentType = typeUtil.getIntentType(elementType);
        String key = getKey(element);
        boolean required = element.getAnnotation(NotRequired.class) == null;
        boolean hasDefault = typeUtil.isPrimitive(elementType);
        boolean needsToBeCast = typeUtil.needToCastIntentType(elementType);

        IntentInjectionAdapterGenerator intentInjectionAdapterGenerator = getOrCreateTargetClass(targetClassMap, enclosingElement);
        IntentFieldBinding binding = new IntentFieldBinding(name, elementType.toString(), intentType, key, needsToBeCast, hasDefault, required);
        intentInjectionAdapterGenerator.addField(binding);

        // Add the type-erased version to the valid targets set.
        erasedTargetNames.add(enclosingElement.toString());
    }

    private String getKey(Element element) {
        if (isDefaultAnnotationElement(element, InjectExtra.class.getName(), "value")) {
            return generateKey(IntentFieldBinding.KEY_PREFIX, element.getSimpleName().toString());
        }
        return element.getAnnotation(InjectExtra.class).value();
    }

    private IntentInjectionAdapterGenerator getOrCreateTargetClass(Map<TypeElement, IntentInjectionAdapterGenerator> targetClassMap,
                                                                   TypeElement enclosingElement) {
        IntentInjectionAdapterGenerator intentInjectionAdapterGenerator = targetClassMap.get(enclosingElement);
        if (intentInjectionAdapterGenerator == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage) + INTENT_ADAPTER_SUFFIX;

            intentInjectionAdapterGenerator = new IntentInjectionAdapterGenerator(classPackage, className, targetType);
            targetClassMap.put(enclosingElement, intentInjectionAdapterGenerator);
        }
        return intentInjectionAdapterGenerator;
    }
}
