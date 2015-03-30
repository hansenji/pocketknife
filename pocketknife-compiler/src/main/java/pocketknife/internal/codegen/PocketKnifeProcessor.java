package pocketknife.internal.codegen;

import com.google.common.collect.ImmutableSet;
import com.squareup.javawriter.JavaWriter;
import pocketknife.InjectArgument;
import pocketknife.InjectExtra;
import pocketknife.IntentBuilder;
import pocketknife.SaveState;
import pocketknife.internal.codegen.builder.IntentBuilderGenerator;
import pocketknife.internal.codegen.builder.IntentBuilderProcessor;
import pocketknife.internal.codegen.injection.BundleInjectionAdapterGenerator;
import pocketknife.internal.codegen.injection.BundleInjectionProcessor;
import pocketknife.internal.codegen.injection.IntentInjectionAdapterGenerator;
import pocketknife.internal.codegen.injection.IntentInjectionProcessor;

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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import static javax.tools.Diagnostic.Kind.ERROR;

public class PocketKnifeProcessor extends AbstractProcessor {


    private Messager messager;
    private Filer filer;

    private BundleInjectionProcessor bundleInjectionProcessor;
    private IntentInjectionProcessor intentInjectionProcessor;
    private IntentBuilderProcessor intentBuilderProcessor;

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
        bundleInjectionProcessor = new BundleInjectionProcessor(messager, elements, types);
        intentInjectionProcessor = new IntentInjectionProcessor(messager, elements, types);
        intentBuilderProcessor = new IntentBuilderProcessor(messager, elements, types);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(
                SaveState.class.getCanonicalName(),
                InjectArgument.class.getCanonicalName(),
                InjectExtra.class.getCanonicalName(),
                IntentBuilder.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Bundle Injections
        Map<TypeElement, BundleInjectionAdapterGenerator> bundleInjectionMap = bundleInjectionProcessor.findAndParseTargets(roundEnv);

        for (Map.Entry<TypeElement, BundleInjectionAdapterGenerator> entry : bundleInjectionMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            BundleInjectionAdapterGenerator bundleInjectionAdapterGenerator = entry.getValue();
            JavaWriter javaWriter = null;
            try {
                JavaFileObject jfo = filer.createSourceFile(bundleInjectionAdapterGenerator.getFqcn(), typeElement);
                javaWriter = new JavaWriter(jfo.openWriter());
                bundleInjectionAdapterGenerator.generate(javaWriter);
            } catch (Exception e) {
                error(typeElement, "Unable to write adapter for type %s: %s", typeElement, e.getMessage());
            } finally {
                close(javaWriter);
            }
        }

        // Intent Injections
        Map<TypeElement, IntentInjectionAdapterGenerator> intentInjectionMap = intentInjectionProcessor.findAndParseTargets(roundEnv);

        for (Map.Entry<TypeElement, IntentInjectionAdapterGenerator> entry : intentInjectionMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            IntentInjectionAdapterGenerator intentInjectionAdapterGenerator = entry.getValue();
            JavaWriter javaWriter = null;
            try {
                JavaFileObject jfo = filer.createSourceFile(intentInjectionAdapterGenerator.getFqcn(), typeElement);
                javaWriter = new JavaWriter(jfo.openWriter());
                intentInjectionAdapterGenerator.generate(javaWriter);
            } catch (Exception e) {
                error(typeElement, "Unable to write adapter for type %s: %s", typeElement, e.getMessage());
            } finally {
                close(javaWriter);
            }
        }

        // Intent Builder
        Map<TypeElement, IntentBuilderGenerator> intentBuilderMap = intentBuilderProcessor.findAndParseTargets(roundEnv);

        for (Map.Entry<TypeElement, IntentBuilderGenerator> entry : intentBuilderMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            IntentBuilderGenerator intentBuilderGenerator = entry.getValue();
            JavaWriter javaWriter = null;
            try {
                JavaFileObject jfo = filer.createSourceFile(intentBuilderGenerator.getFqcn(), typeElement);
                javaWriter = new JavaWriter(jfo.openWriter());
                intentBuilderGenerator.generate(javaWriter);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                error(typeElement, "Unable to write adapter for type %s: %s", typeElement, stackTrace.toString());
            } finally {
                close(javaWriter);
            }
        }

        return false;
    }

    private void close(JavaWriter javaWriter) {
        if (javaWriter != null) {
            try {
                javaWriter.close();
            } catch (IOException e) {
                error(null, e.getMessage());
            }
        }
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        messager.printMessage(ERROR, message, element);
    }

}
