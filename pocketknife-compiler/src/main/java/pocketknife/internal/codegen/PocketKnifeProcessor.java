package pocketknife.internal.codegen;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.JavaFile;
import pocketknife.BindArgument;
import pocketknife.BundleBuilder;
import pocketknife.FragmentBuilder;
import pocketknife.BindExtra;
import pocketknife.IntentBuilder;
import pocketknife.SaveState;
import pocketknife.internal.codegen.builder.BuilderGenerator;
import pocketknife.internal.codegen.builder.BuilderProcessor;
import pocketknife.internal.codegen.binding.BundleBindingAdapterGenerator;
import pocketknife.internal.codegen.binding.BundleBindingProcessor;
import pocketknife.internal.codegen.binding.IntentBindingAdapterGenerator;
import pocketknife.internal.codegen.binding.IntentBindingProcessor;

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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import static javax.tools.Diagnostic.Kind.ERROR;

public class PocketKnifeProcessor extends AbstractProcessor {


    private Messager messager;
    private Filer filer;

    private BundleBindingProcessor bundleBindingProcessor;
    private IntentBindingProcessor intentBindingProcessor;
    private BuilderProcessor builderProcessor;

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
        bundleBindingProcessor = new BundleBindingProcessor(messager, elements, types);
        intentBindingProcessor = new IntentBindingProcessor(messager, elements, types);
        builderProcessor = new BuilderProcessor(messager, elements, types);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(
                SaveState.class.getCanonicalName(),
                BindArgument.class.getCanonicalName(),
                BindExtra.class.getCanonicalName(),
                IntentBuilder.class.getCanonicalName(),
                BundleBuilder.class.getCanonicalName(),
                FragmentBuilder.class.getCanonicalName()
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Bundle bindings
        Map<TypeElement, BundleBindingAdapterGenerator> bundleBindingMap = bundleBindingProcessor.findAndParseTargets(roundEnv);

        for (Map.Entry<TypeElement, BundleBindingAdapterGenerator> entry : bundleBindingMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            BundleBindingAdapterGenerator generator = entry.getValue();
            try {
                JavaFile javaFile = generator.generate();
                javaFile.writeTo(filer);
            } catch (Exception e) {
                error(typeElement, "Unable to write adapter for type %s: %s", typeElement, e.getMessage());
            }
        }

        // Intent bindings
        Map<TypeElement, IntentBindingAdapterGenerator> intentBindingMap = intentBindingProcessor.findAndParseTargets(roundEnv);

        for (Map.Entry<TypeElement, IntentBindingAdapterGenerator> entry : intentBindingMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            IntentBindingAdapterGenerator generator = entry.getValue();
            try {
                JavaFile javaFile = generator.generate();
                javaFile.writeTo(filer);
            } catch (Exception e) {
                error(typeElement, "Unable to write adapter for type %s: %s", typeElement, e.getMessage());
            }
        }

        // Builders
        Map<TypeElement, BuilderGenerator> builderMap = builderProcessor.findAndParseTargets(roundEnv);

        for (Map.Entry<TypeElement, BuilderGenerator> entry : builderMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            BuilderGenerator generator = entry.getValue();
            try {
                JavaFile javaFile = generator.generate();
                javaFile.writeTo(filer);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                error(typeElement, "Unable to write builder for type %s: %s", typeElement, stackTrace.toString());
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
