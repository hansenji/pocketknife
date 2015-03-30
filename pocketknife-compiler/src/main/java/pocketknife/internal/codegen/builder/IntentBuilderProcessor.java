package pocketknife.internal.codegen.builder;

import pocketknife.IntentBuilder;
import pocketknife.internal.codegen.InvalidTypeException;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static javax.lang.model.element.ElementKind.METHOD;

public class IntentBuilderProcessor extends BuilderProcessor {


    private static final String EXTRA_KEY_PREFIX = "EXTRA_";
    private static final String INTENT_BUILDER = IntentBuilder.class.getName();

    public IntentBuilderProcessor(Messager messager, Elements elements, Types types) {
        super(messager, elements, types);
    }

    public Map<TypeElement, IntentBuilderGenerator> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, IntentBuilderGenerator> targetMap = new LinkedHashMap<TypeElement, IntentBuilderGenerator>();

        // Process each @IntentBuilder
        for (Element element : env.getElementsAnnotatedWith(IntentBuilder.class)) {
            try {
                parseIntentBuilder(element, targetMap);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                error(element, "Unable to generate @%s.\n\n%s", IntentBuilder.class.getSimpleName(), stackTrace.toString());
            }
        }

        return targetMap;
    }

    private void parseIntentBuilder(Element element, Map<TypeElement, IntentBuilderGenerator> targetMap) throws InvalidTypeException {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        // This should be guarded by the annotation's @Target but it's worth a check for safe casting.
        if (!(element instanceof ExecutableElement) || element.getKind() != METHOD) {
            throw new IllegalStateException(String.format("@%s annotation must be on a method.", IntentBuilder.class));
        }
        ExecutableElement executableElement = (ExecutableElement) element;
        // Validate
        if (!types.isAssignable(executableElement.getReturnType(), typeUtil.intentType)) {
            throw new IllegalStateException("Method must return an Intent");
        }

        validateIntentBuilderArguments(executableElement);
        validateEnclosingClass(IntentBuilder.class, enclosingElement);
        validateBindingPackage(IntentBuilder.class, element);

        IntentBuilderMethodBinding methodBinding = getMethodBinding(executableElement);
        IntentBuilderGenerator generator = getOrCreateTargetClass(targetMap, enclosingElement);
        generator.addMethod(methodBinding);
    }

    private void validateIntentBuilderArguments(Element element) {
        IntentBuilder intentBuilder = element.getAnnotation(IntentBuilder.class);
        if (intentBuilder != null && intentBuilder.action().trim().isEmpty() && void.class.getName().equals(getIntentBuilderClsValue(element))) {
            throw new IllegalStateException(String.format("@%s must have cls or action specified", IntentBuilder.class.getSimpleName()));
        }
    }

    private String getIntentBuilderClsValue(Element element) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (INTENT_BUILDER.equals(annotationMirror.getAnnotationType().toString())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
                    if ("cls".equals(entry.getKey().getSimpleName().toString())) {
                        return entry.getValue().getValue().toString();
                    }
                }
                // If no cls is found return default
                return void.class.getName();
            }
        }
        throw new IllegalStateException(String.format("Unable to find @IntentBuilder for %s", element.getSimpleName()));
    }

    private IntentBuilderMethodBinding getMethodBinding(ExecutableElement element) throws InvalidTypeException {
        IntentBuilder intentBuilder = element.getAnnotation(IntentBuilder.class);
        IntentBuilderMethodBinding binding = new IntentBuilderMethodBinding(element.getSimpleName().toString(), getIntentBuilderClsValue(element),
                intentBuilder.action(), intentBuilder.data(), intentBuilder.flags(), intentBuilder.categories(), intentBuilder.type());
        List<? extends Element> parameters = element.getParameters();
        for (Element parameter : parameters) {
            binding.addField(getFieldBinding(parameter));
        }
        return binding;
    }

    private IntentFieldBinding getFieldBinding(Element parameter) throws InvalidTypeException {
        TypeMirror parameterType = parameter.asType();
        if (parameterType instanceof TypeVariable) {
            parameterType = ((TypeVariable) parameterType).getUpperBound();
        }

        String name = parameter.getSimpleName().toString();
        String type = parameterType.toString();
        String intentType = typeUtil.getIntentType(parameterType);
        String key = generateKey(EXTRA_KEY_PREFIX, name);
        boolean arrayList = isIntentArrayList(intentType);
        return new IntentFieldBinding(name, type, intentType, key, arrayList);
    }

    private boolean isIntentArrayList(String intentType) {
        return "CharSequenceArrayList".equals(intentType)
                || "IntegerArrayList".equals(intentType)
                || "ParcelableArrayList".equals(intentType)
                || "StringArrayList".equals(intentType);
    }

    private IntentBuilderGenerator getOrCreateTargetClass(Map<TypeElement, IntentBuilderGenerator> targetMap, TypeElement element) {
        IntentBuilderGenerator generator = targetMap.get(element);
        if (generator == null) {
            String interfaceName = element.getQualifiedName().toString();
            String classPackage = getPackageName(element);
            String className = GENERATOR_PREFIX + getClassName(element, classPackage);

            generator = new IntentBuilderGenerator(classPackage, className, interfaceName);
            targetMap.put(element, generator);
        }
        return generator;
    }


}
