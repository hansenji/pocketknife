package pocketknife.internal.codegen;

import android.os.Build;
import com.squareup.javawriter.JavaWriter;
import pocketknife.InjectArgument;
import pocketknife.SaveState;
import pocketknife.internal.GeneratedAdapters;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;
import static pocketknife.internal.GeneratedAdapters.ANDROID_PREFIX;
import static pocketknife.internal.GeneratedAdapters.JAVA_PREFIX;

public class PocketKnifeProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elements;
    private Types types;
    private Filer filer;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>(Arrays.asList(SaveState.class.getCanonicalName(), InjectArgument.class.getCanonicalName()));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, BundleAdapterGenerator> targetClassMap = findAndParseTargets(roundEnv);

        for (Map.Entry<TypeElement, BundleAdapterGenerator> entry : targetClassMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            BundleAdapterGenerator bundleAdapterGenerator = entry.getValue();
            JavaWriter javaWriter = null;
            try {
                JavaFileObject jfo = filer.createSourceFile(bundleAdapterGenerator.getFqcn(), typeElement);
                javaWriter = new JavaWriter(jfo.openWriter());
                bundleAdapterGenerator.generate(javaWriter);
            } catch (Exception e) {
                error(typeElement, "Unable to write injector for type %s: %s", typeElement, e.getMessage());
            } finally {
                if (javaWriter != null) {
                    try {
                        javaWriter.close();
                    } catch (IOException e) {
                        error(null, e.getMessage());
                    }
                }
            }
        }


        return false;
    }

    private Map<TypeElement, BundleAdapterGenerator> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, BundleAdapterGenerator> targetClassMap = new LinkedHashMap<TypeElement, BundleAdapterGenerator>();
        Set<String> erasedTargetNames = new LinkedHashSet<String>();

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

        return targetClassMap;
    }

    private void parseSaveState(Element element, Map<TypeElement, BundleAdapterGenerator> targetClassMap, Set<String> erasedTargetNames)
            throws ClassNotFoundException {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target has all the appropriate information for type
        TypeMirror elementType = element.asType();
        if (elementType instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) elementType;
            elementType = typeVariable.getUpperBound();
        }

        boolean hasError = isSaveStateArgumentsInvalid(element);
        hasError |= isInvalidForGeneratedCode(SaveState.class, "fields", element);
        hasError |= isBindingInWrongPackage(SaveState.class, element);

        if (hasError) {
            return;
        }

        // Assemble information on the injection point.
        String name = element.getSimpleName().toString();
        SaveState annotation = element.getAnnotation(SaveState.class);
        String defaultValue = annotation.defaultValue();
        int minSdk = annotation.minSdk();

        BundleAdapterGenerator bundleAdapterGenerator = getOrCreateTargetClass(targetClassMap, enclosingElement);
        BundleFieldBinding binding = new BundleFieldBinding(name, elementType, defaultValue, minSdk);
        bundleAdapterGenerator.addField(binding);

        // Add the type-erased version to the valid injection targets set.
        erasedTargetNames.add(enclosingElement.toString());
    }

    private void parseInjectAnnotation(Element element, Map<TypeElement, BundleAdapterGenerator> targetClassMap, Set<String> erasedTargetNames) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target has all the appropriate information for type
        TypeMirror elementType = element.asType();
        if (element instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) elementType;
            elementType = typeVariable.getUpperBound();
        }

        boolean hasError = isInjectArugmentArgumentsInvalid(element);
        hasError |= isInvalidForGeneratedCode(InjectArgument.class, "fields", element);
        hasError |= isBindingInWrongPackage(InjectArgument.class, element);

        if (hasError) {
            return;
        }

        // Assemble information on the injection point
        String name = element.getSimpleName().toString();
        InjectArgument annotation = element.getAnnotation(InjectArgument.class);
        String key = annotation.key();
        String defaultValue = annotation.defaultValue();
        int minSdk = annotation.minSdk();

        BundleAdapterGenerator bundleAdapterGenerator = getOrCreateTargetClass(targetClassMap, enclosingElement);
        BundleFieldBinding binding = new BundleFieldBinding(name, elementType, key, defaultValue, minSdk);
        bundleAdapterGenerator.addField(binding);

        // Add the type-erased version to the valid injection targets set.
        erasedTargetNames.add(enclosingElement.toString());
    }

    private boolean isSaveStateArgumentsInvalid(Element element) {
        if (element.getAnnotation(SaveState.class).minSdk() < Build.VERSION_CODES.FROYO) {
            error(element, "SaveState.minSdk must be FROYO(8)+");
            return true;
        }
        return false;
    }

    private boolean isInjectArugmentArgumentsInvalid(Element element) {
        InjectArgument annotation = element.getAnnotation(InjectArgument.class);
        if (annotation.minSdk() < Build.VERSION_CODES.FROYO) {
            error(element, "InjectAnnotation.minSdk must be FROYO(8)+");
            return true;
        }
        if (annotation.key() == null || annotation.key().trim().isEmpty()) {
            error(element, "InjectAnnotation.key must not be empty");
            return true;
        }
        return false;
    }

    private boolean hasAllNeededValues(Element element, TypeMirror elementType) {
        return true;
    }


    private boolean isInvalidForGeneratedCode(Class<? extends Annotation> annotationClass, String targetThing, Element element) {
        boolean hasError = false;
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify method modifiers
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
            error(element, "@%s %s must not be private or static. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            hasError = true;
        }

        // Verify Containing type.
        if (enclosingElement.getKind() != CLASS) {
            error(enclosingElement, "@%s %s may only be contained in classes. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            hasError = true;
        }

        // Verify containing class visibility is not private
        if (enclosingElement.getModifiers().contains(PRIVATE)) {
            error(enclosingElement, "@%s %s may not be contained in private classes (%s.%s)", annotationClass.getSimpleName(), targetThing,
                    enclosingElement.getQualifiedName(), element.getSimpleName());
            hasError = true;
        }

        return hasError;
    }

    private boolean isBindingInWrongPackage(Class<? extends Annotation> annotationClass,
                                            Element element) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        String qualifiedName = enclosingElement.getQualifiedName().toString();

        if (qualifiedName.startsWith(ANDROID_PREFIX)) {
            error(element, "@%s-annotated class incorrectly in Android framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return true;
        }
        if (qualifiedName.startsWith(JAVA_PREFIX)) {
            error(element, "@%s-annotated class incorrectly in Java framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return true;
        }

        return false;
    }

    private BundleAdapterGenerator getOrCreateTargetClass(Map<TypeElement, BundleAdapterGenerator> targetClassMap, TypeElement enclosingElement) {
        BundleAdapterGenerator bundleAdapterGenerator = targetClassMap.get(enclosingElement);
        if (bundleAdapterGenerator == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage) + GeneratedAdapters.BUNDLE_ADAPTER_SUFFIX;

            bundleAdapterGenerator = new BundleAdapterGenerator(classPackage, className, targetType, elements, types);
            targetClassMap.put(enclosingElement, bundleAdapterGenerator);
        }
        return bundleAdapterGenerator;
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        messager.printMessage(ERROR, message, element);
    }

    private String getPackageName(TypeElement type) {
        return elements.getPackageOf(type).getQualifiedName().toString();
    }

    private String getClassName(TypeElement typeElement, String packageName) {
        int packageLen = packageName.length() + 1;
        return typeElement.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }
}
