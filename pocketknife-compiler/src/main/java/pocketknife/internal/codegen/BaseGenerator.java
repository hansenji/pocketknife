package pocketknife.internal.codegen;

import com.squareup.javapoet.AnnotationSpec;

import javax.annotation.Generated;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BaseGenerator {

    protected final TypeUtil typeUtil;

    public BaseGenerator(TypeUtil typeUtil) {
        this.typeUtil = typeUtil;
    }

    protected AnnotationSpec getGeneratedAnnotationSpec(Class<? extends BaseGenerator> generator) {
        return AnnotationSpec.builder(Generated.class)
                .addMember("value", "$S", generator.getName())
                .addMember("date", "$S", new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.getDefault()).format(new Date()))
                .build();
    }

}
