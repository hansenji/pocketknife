package pocketknife.internal.codegen;

import com.squareup.javawriter.StringLiteral;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class BaseGenerator {

    public Map<String, Object> getGeneratedMap(Class<?> generatorClass) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("value", StringLiteral.forValue(generatorClass.getName()));
        map.put("date", StringLiteral.forValue(new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.getDefault()).format(new Date())));
        return map;
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
