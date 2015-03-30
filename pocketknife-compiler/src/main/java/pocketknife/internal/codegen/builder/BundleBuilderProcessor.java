package pocketknife.internal.codegen.builder;

import pocketknife.BundleBuilder;
import pocketknife.internal.codegen.InvalidTypeException;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
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

public class BundleBuilderProcessor extends BuilderProcessor {

    private static final String ARG_KEY_PREFIX = "ARG_";

    public BundleBuilderProcessor(Messager messager, Elements elements, Types types) {
        super(messager, elements, types);
    }

    public Map<TypeElement, BundleBuilderGenerator> findAndParseTargets(RoundEnvironment roundEnv) {
        Map<TypeElement, BundleBuilderGenerator> targetMap = new LinkedHashMap<TypeElement, BundleBuilderGenerator>();

        // Process each @BundleBuilder
        for (Element element : roundEnv.getElementsAnnotatedWith(BundleBuilder.class)) {
            try {
                parseBundleBuilder(element, targetMap);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                error(element, "Unable to generate @%s.\n\n%s", BundleBuilder.class.getSimpleName(), stackTrace.toString());
            }
        }

        return targetMap;
    }

    private void parseBundleBuilder(Element element, Map<TypeElement, BundleBuilderGenerator> targetMap) throws InvalidTypeException {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        if (!(element instanceof ExecutableElement) || element.getKind() != METHOD) {
            throw new IllegalStateException(String.format("@%s annotation must be on a method.", BundleBuilder.class));
        }
        ExecutableElement executableElement = (ExecutableElement) element;
        // Validate
        if (!types.isAssignable(executableElement.getReturnType(), typeUtil.bundleType)) {
            throw new IllegalStateException("Method must return an Bundle");
        }

        validateEnclosingClass(BundleBuilder.class, enclosingElement);
        validateBindingPackage(BundleBuilder.class, element);

        BundleBuilderMethodBinding methodBinding = getMethodBinding(executableElement);
        BundleBuilderGenerator generator = getOrCreateTargetClass(targetMap, enclosingElement);
        generator.addMethod(methodBinding);
    }

    private BundleBuilderMethodBinding getMethodBinding(ExecutableElement element) throws InvalidTypeException {
        BundleBuilderMethodBinding binding = new BundleBuilderMethodBinding(element.getSimpleName().toString());
        List<? extends VariableElement> parameters = element.getParameters();
        for (Element parameter : parameters) {
            binding.addField(getFieldBinding(parameter));
        }
        return binding;
    }

    private BundleFieldBinding getFieldBinding(Element parameter) throws InvalidTypeException {
        TypeMirror parameterType = parameter.asType();
        if (parameterType instanceof TypeVariable) {
            parameterType = ((TypeVariable) parameterType).getUpperBound();
        }

        String name = parameter.getSimpleName().toString();
        String type = parameterType.toString();
        String bundleType = typeUtil.getBundleType(parameterType);
        String key = generateKey(ARG_KEY_PREFIX, name);
        return new BundleFieldBinding(name, type, bundleType, key);
    }

    private BundleBuilderGenerator getOrCreateTargetClass(Map<TypeElement, BundleBuilderGenerator> targetMap, TypeElement element) {
        BundleBuilderGenerator generator = targetMap.get(element);
        if (generator == null) {
            String interfaceName = element.getQualifiedName().toString();
            String classPackage = getPackageName(element);
            String className = GENERATOR_PREFIX + getClassName(element, classPackage);

            generator = new BundleBuilderGenerator(classPackage, className, interfaceName);
            targetMap.put(element, generator);
        }
        return generator;
    }
}
