package pocketknife.internal.codegen;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class KeySpec implements Comparable<KeySpec> {
    private String name;
    private String value;

    public KeySpec(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        KeySpec keySpec = (KeySpec) o;

        return new EqualsBuilder()
                .append(name, keySpec.name)
                .append(value, keySpec.value)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(value)
                .toHashCode();
    }

    @Override
    public int compareTo(KeySpec o) {
        int i = name.compareTo(o.name);
        if (i == 0) {
            return value.compareTo(o.value);
        }
        return i;
    }
}
