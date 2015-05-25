package pocketknife.internal.codegen.builder;

import pocketknife.BundleBuilder;
import pocketknife.FragmentBuilder;
import pocketknife.IntentBuilder;
import pocketknife.internal.codegen.BaseProcessor;
import pocketknife.internal.codegen.BundleFieldBinding;
import pocketknife.internal.codegen.IntentFieldBinding;
import pocketknife.internal.codegen.InvalidTypeException;
import pocketknife.internal.codegen.TypeUtil;

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
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.tools.Diagnostic.Kind.ERROR;
import static pocketknife.internal.GeneratedAdapters.ANDROID_PREFIX;
import static pocketknife.internal.GeneratedAdapters.JAVA_PREFIX;

public class BuilderProcessor extends BaseProcessor {

    protected static final String GENERATOR_PREFIX = "PocketKnife";
    private static final String ARG_KEY_PREFIX = "ARG_";
    private static final String EXTRA_KEY_PREFIX = "EXTRA_";

    protected final Messager messager;
    protected final Elements elements;
    protected final Types types;
    protected final TypeUtil typeUtil;

    public BuilderProcessor(Messager messager, Elements elements, Types types) {
        this.messager = messager;
        this.elements = elements;
        this.types = types;
        this.typeUtil = TypeUtil.getInstance(elements, types);
    }

    protected void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        messager.printMessage(ERROR, message, element);
    }

    protected void validateEnclosingClass(Class<? extends Annotation> annotationClass, TypeElement enclosingElement) {
        if (enclosingElement.getKind() != INTERFACE) {
            throw new IllegalStateException(String.format("@%s must be in an interface", annotationClass.getSimpleName()));
        }
        if (enclosingElement.getModifiers().contains(PRIVATE)) {
            throw new IllegalStateException(String.format("@%s may not be contained in private interface", annotationClass.getSimpleName()));
        }
    }

    protected void validateBindingPackage(Class<? extends Annotation> annotationClass, Element element) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        String qualifiedName = enclosingElement.getQualifiedName().toString();

        if (qualifiedName.startsWith(ANDROID_PREFIX)) {
            throw new IllegalStateException(String.format("@%s-annotated interface incorrectly in Android framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName));
        }
        if (qualifiedName.startsWith(JAVA_PREFIX)) {
            throw new IllegalStateException(String.format("@%s-annotated interface incorrectly in Java framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName));
        }
    }

    protected String getPackageName(TypeElement type) {
        return elements.getPackageOf(type).getQualifiedName().toString();
    }

    protected String getClassName(TypeElement typeElement, String packageName) {
        int packageLen = packageName.length() + 1;
        return typeElement.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    public Map<TypeElement, BuilderGenerator> findAndParseTargets(RoundEnvironment roundEnv) {
        Map<TypeElement, BuilderGenerator> targetMap = new LinkedHashMap<TypeElement, BuilderGenerator>();

        // @BundleBuilder
        processBundleBuilder(targetMap, roundEnv);

        // @IntentBuilder
        processIntentBuilder(targetMap, roundEnv);

        // @FragmentBuilder
        processFragmentBuilder(targetMap, roundEnv);

        return targetMap;
    }

    private void processBundleBuilder(Map<TypeElement, BuilderGenerator> targetMap, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(BundleBuilder.class)) {
            try {
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

                BundleMethodBinding methodBinding = getBundleMethodBinding(executableElement);
                BuilderGenerator generator = getOrCreateTargetClass(targetMap, enclosingElement);
                generator.addMethod(methodBinding);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                error(element, "Unable to generate @%s.\n\n%s", BundleBuilder.class.getSimpleName(), stackTrace.toString());
            }
        }
    }

    private BundleMethodBinding getBundleMethodBinding(ExecutableElement element) throws InvalidTypeException {
        BundleMethodBinding binding = new BundleMethodBinding(element.getSimpleName().toString());
        for (Element parameter : element.getParameters()) {
            binding.addField(getBundleFieldBinding(parameter));
        }
        return binding;
    }

    private BundleFieldBinding getBundleFieldBinding(Element element) throws InvalidTypeException {
        TypeMirror type = element.asType();
        if (type instanceof TypeVariable) {
            type = ((TypeVariable) type).getUpperBound();
        }

        String name = element.getSimpleName().toString();
        String bundleType = typeUtil.getBundleType(type);
        String key = generateKey(ARG_KEY_PREFIX, name);
        return new BundleFieldBinding(name, type, bundleType, key);
    }

    private void processIntentBuilder(Map<TypeElement, BuilderGenerator> targetMap, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(IntentBuilder.class)) {
            try {
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

                IntentMethodBinding methodBinding = getIntentMethodBinding(executableElement);
                BuilderGenerator generator = getOrCreateTargetClass(targetMap, enclosingElement);
                generator.addMethod(methodBinding);
            } catch (InvalidTypeException e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                error(element, "Unable to generate @%s.\n\n%s", IntentBuilder.class.getSimpleName(), stackTrace.toString());
            }
        }
    }

    private void validateIntentBuilderArguments(Element element) {
        IntentBuilder intentBuilder = element.getAnnotation(IntentBuilder.class);

        if (intentBuilder != null && isDefaultAnnotationElement(element, IntentBuilder.class.getName(), "action")
                && getIntentBuilderClsValue(element) == null) {
            throw new IllegalStateException(String.format("@%s must have cls or action specified", IntentBuilder.class.getSimpleName()));
        }
    }

    private TypeMirror getIntentBuilderClsValue(Element element) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (IntentBuilder.class.getName().equals(annotationMirror.getAnnotationType().toString())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
                    if ("cls".equals(entry.getKey().getSimpleName().toString())) {
                        return (TypeMirror) entry.getValue().getValue();
                    }
                }
                // If no cls is found return default
                return null;
            }
        }
        throw new IllegalStateException(String.format("Unable to find @IntentBuilder for %s", element.getSimpleName()));
    }

    private IntentMethodBinding getIntentMethodBinding(ExecutableElement element) throws InvalidTypeException {
        IntentBuilder intentBuilder = element.getAnnotation(IntentBuilder.class);
        String intentBuilderName = IntentBuilder.class.getName();
        String action = null;
        if (!isDefaultAnnotationElement(element, intentBuilderName, "action")) {
            action = intentBuilder.action();
        }
        String data = null; // TODO move to its own annotation
        if (!isDefaultAnnotationElement(element, intentBuilderName, "data")) {
            data = intentBuilder.data();
        }
        Integer flags = null;
        if (!isDefaultAnnotationElement(element, intentBuilderName, "flags")) {
            flags = intentBuilder.flags();
        }
        String type = null;
        if (!isDefaultAnnotationElement(element, intentBuilderName, "type")) {
            type = intentBuilder.type();
        }

        IntentMethodBinding binding = new IntentMethodBinding(element.getSimpleName().toString(), getIntentBuilderClsValue(element), action,
                data, flags, intentBuilder.categories(), type);
        for (Element parameter : element.getParameters()) {
            binding.addField(getIntentFieldBinding(parameter));
        }
        return binding;
    }

    private IntentFieldBinding getIntentFieldBinding(Element element) throws InvalidTypeException {
        TypeMirror type = element.asType();
        if (type instanceof TypeVariable) {
            type = ((TypeVariable) type).getUpperBound();
        }

        String name = element.getSimpleName().toString();
        String intentType = typeUtil.getIntentType(type);
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

    private void processFragmentBuilder(Map<TypeElement, BuilderGenerator> targetMap, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(FragmentBuilder.class)) {
            try {
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
                if (!(element instanceof ExecutableElement) || element.getKind() != METHOD) {
                    throw new IllegalStateException(String.format("@%s annotation must be on a method.", FragmentBuilder.class));
                }
                ExecutableElement executableElement = (ExecutableElement) element;
                // Validate
                if (!types.isAssignable(executableElement.getReturnType(), typeUtil.fragmentType) &&
                        !types.isAssignable(executableElement.getReturnType(), typeUtil.supportFragmentType)) {
                    throw new IllegalStateException("Method must return a Fragment or Support Fragment");
                }

                validateEnclosingClass(FragmentBuilder.class, enclosingElement);
                validateBindingPackage(FragmentBuilder.class, element);

                FragmentMethodBinding methodBinding = getFragmentMethodBinding(executableElement);
                BuilderGenerator generator = getOrCreateTargetClass(targetMap, enclosingElement);
                generator.addMethod(methodBinding);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                error(element, "Unable to generate @%s.\n\n%s", FragmentBuilder.class.getSimpleName(), stackTrace.toString());
            }
        }
    }

    private FragmentMethodBinding getFragmentMethodBinding(ExecutableElement element) throws InvalidTypeException {
        FragmentMethodBinding binding = new FragmentMethodBinding(element.getSimpleName().toString(), element.getReturnType());
        for (Element parameter : element.getParameters()) {
            binding.addField(getBundleFieldBinding(parameter));
        }
        return binding;
    }

    private BuilderGenerator getOrCreateTargetClass(Map<TypeElement, BuilderGenerator> targetMap, TypeElement element) {
        BuilderGenerator generator = targetMap.get(element);
        if (generator == null) {
            String interfaceName = element.getQualifiedName().toString();
            String classPackage = getPackageName(element);
            String className = GENERATOR_PREFIX + getClassName(element, classPackage);

            generator = new BuilderGenerator(classPackage, className, interfaceName);
            targetMap.put(element, generator);
        }
        return generator;
    }
}
