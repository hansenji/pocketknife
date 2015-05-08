package pocketknife.internal.codegen;

import com.squareup.javapoet.AnnotationSpec;

import javax.annotation.Generated;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class BaseGenerator {

    protected AnnotationSpec getGeneratedAnnotationSpec(Class<? extends BaseGenerator> generator) {
        return AnnotationSpec.builder(Generated.class)
                .addMember("value", "$S", generator.getName())
                .addMember("date", "$S", new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.getDefault()).format(new Date()))
                .build();
    }

    protected String getReturnVarName(String returnVarNameRoot, MethodBinding method) {
        Set<String> fieldNames = new LinkedHashSet<String>();
        for (FieldBinding fieldBinding : method.getFields()) {
            fieldNames.add(fieldBinding.getName());
        }

        String returnVarName = returnVarNameRoot;
        int count = 0;
        while (fieldNames.contains(returnVarName)) {
            returnVarName = returnVarNameRoot + ++count;
        }
        return returnVarName;
    }
}
