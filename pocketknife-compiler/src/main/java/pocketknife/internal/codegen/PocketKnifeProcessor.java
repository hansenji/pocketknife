package pocketknife.internal.codegen;

import com.squareup.javawriter.JavaWriter;
import pocketknife.InjectArgument;
import pocketknife.SaveState;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static javax.tools.Diagnostic.Kind.ERROR;

public class PocketKnifeProcessor extends AbstractProcessor {



    private Messager messager;
    private Filer filer;

    private BundleProcessor bundleProcessor;
    private IntentProcessor intentProcessor;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        Elements elements = processingEnv.getElementUtils();
        Types types = processingEnv.getTypeUtils();
        bundleProcessor = new BundleProcessor(messager, elements, types);
        intentProcessor = new IntentProcessor(messager, elements, types);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>(Arrays.asList(SaveState.class.getCanonicalName(), InjectArgument.class.getCanonicalName()));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Bundle Injections
        Map<TypeElement, BundleAdapterGenerator> bundleTargetClassMap = bundleProcessor.findAndParseTargets(roundEnv);

        for (Map.Entry<TypeElement, BundleAdapterGenerator> entry : bundleTargetClassMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            BundleAdapterGenerator bundleAdapterGenerator = entry.getValue();
            JavaWriter javaWriter = null;
            try {
                JavaFileObject jfo = filer.createSourceFile(bundleAdapterGenerator.getFqcn(), typeElement);
                javaWriter = new JavaWriter(jfo.openWriter());
                bundleAdapterGenerator.generate(javaWriter);
            } catch (Exception e) {
                error(typeElement, "Unable to write adapter for type %s: %s", typeElement, e.getMessage());
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

        // Intent Injections
        Map<TypeElement, IntentAdapterGenerator> intentTargetClassMap = intentProcessor.findAndParseTargets(roundEnv);

        for (Map.Entry<TypeElement, IntentAdapterGenerator> entry : intentTargetClassMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            IntentAdapterGenerator intentAdapterGenerator = entry.getValue();
            JavaWriter javaWriter = null;
            try {
                JavaFileObject jfo = filer.createSourceFile(intentAdapterGenerator.getFqcn(), typeElement);
                javaWriter = new JavaWriter(jfo.openWriter());
                intentAdapterGenerator.generate(javaWriter);
            } catch (Exception e) {
                error(typeElement, "Unable to write adapter for type %s: %s", typeElement, e.getMessage());
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

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        messager.printMessage(ERROR, message, element);
    }

}
