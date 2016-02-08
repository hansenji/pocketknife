package pocketknife.internal.codegen.binding;

import pocketknife.BindExtra;
import pocketknife.IntentSerializer;
import pocketknife.NotRequired;
import pocketknife.PocketKnifeIntentSerializer;
import pocketknife.internal.codegen.Access;
import pocketknife.internal.codegen.IntentFieldBinding;
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

import static pocketknife.internal.GeneratedAdapters.INTENT_ADAPTER_SUFFIX;

public class IntentBindingProcessor extends BindingProcessor {

    public IntentBindingProcessor(Messager messager, Elements elements, Types types) {
        super(messager, elements, types);
    }

    public Map<TypeElement, IntentBindingAdapterGenerator> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, IntentBindingAdapterGenerator> targetClassMap = new LinkedHashMap<TypeElement, IntentBindingAdapterGenerator>();
        Set<String> erasedTargetNames = new LinkedHashSet<String>();

        // Process each @BindExtra
        for (Element element : env.getElementsAnnotatedWith(BindExtra.class)) {
            BindExtra annotation = element.getAnnotation(BindExtra.class);
            if (annotation == null) {
                continue;
            }
            try {
                parseBindExtra(element, targetClassMap, erasedTargetNames);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                error(element, "Unable to generate intent adapter for @BindExtra.\n\n%s", stackTrace);
            }
        }

        // Try to find a parent adapter for each adapter
        for (Map.Entry<TypeElement, IntentBindingAdapterGenerator> entry : targetClassMap.entrySet()) {
            TypeElement parent = findParent(entry.getKey(), erasedTargetNames);
            if (parent != null) {
                entry.getValue().setParentAdapter(getPackageName(parent), parent.getSimpleName() + INTENT_ADAPTER_SUFFIX);
            }
        }

        return targetClassMap;
    }

    private void parseBindExtra(Element element, Map<TypeElement, IntentBindingAdapterGenerator> targetClassMap, Set<String> erasedTargetNames)
            throws InvalidTypeException {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target has all the appropriate information for type
        TypeMirror type = element.asType();
        if (type instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) type;
            type = typeVariable.getUpperBound();
        }

        TypeMirror intentSerializer = getAnnotationElementClass(element, IntentSerializer.class);
        validateSerializer(element, IntentSerializer.class, intentSerializer, PocketKnifeIntentSerializer.class);

        validateNotRequiredArguments(element);
        validateBindingPackage(BindExtra.class, element);
        validateForCodeGeneration(BindExtra.class, element);
        Access access = getAccess(BindExtra.class, element, enclosingElement);

        // Assemble information on the bind point
        String name = element.getSimpleName().toString();
        String intentType = null;
        KeySpec key = getKey(element);
        boolean required = element.getAnnotation(NotRequired.class) == null;
        boolean hasDefault = false;
        boolean needsToBeCast = false;
        if (intentSerializer == null) {
            intentType = typeUtil.getIntentType(type);
            hasDefault = typeUtil.isPrimitive(type);
            needsToBeCast = typeUtil.needToCastIntentType(type);
        }

        IntentBindingAdapterGenerator intentBindingAdapterGenerator = getOrCreateTargetClass(targetClassMap, enclosingElement);
        IntentFieldBinding binding = new IntentFieldBinding(name, access, type, intentType, key, needsToBeCast, hasDefault, required, intentSerializer);
        intentBindingAdapterGenerator.addField(binding);

        // Add the type-erased version to the valid targets set.
        erasedTargetNames.add(enclosingElement.toString());
    }

    private KeySpec getKey(Element element) {
        if (isDefaultAnnotationElement(element, BindExtra.class.getName(), "value")) {
            return new KeySpec(null, generateKey(IntentFieldBinding.KEY_PREFIX, element.getSimpleName().toString()));
        }
        return new KeySpec(null, element.getAnnotation(BindExtra.class).value());
    }

    private IntentBindingAdapterGenerator getOrCreateTargetClass(Map<TypeElement, IntentBindingAdapterGenerator> targetClassMap,
                                                                   TypeElement enclosingElement) {
        IntentBindingAdapterGenerator intentBindingAdapterGenerator = targetClassMap.get(enclosingElement);
        if (intentBindingAdapterGenerator == null) {
            TypeMirror targetType = enclosingElement.asType();
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage) + INTENT_ADAPTER_SUFFIX;

            intentBindingAdapterGenerator = new IntentBindingAdapterGenerator(classPackage, className, targetType, typeUtil);
            targetClassMap.put(enclosingElement, intentBindingAdapterGenerator);
        }
        return intentBindingAdapterGenerator;
    }
}
