package pocketknife.internal.codegen;

public class Access {
    public enum Type {
        METHOD,
        FIELD
    }

    private Type type;
    private String getter;
    private String setter;

    public Access(Type type, String getter, String setter) {
        this.type = type;
        this.getter = getter;
        this.setter = setter;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getGetter() {
        return getter;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public String getSetter() {
        return setter;
    }

    public void setSetter(String setter) {
        this.setter = setter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Access access = (Access) o;

        if (type != access.type) {
            return false;
        }
        return getter.equals(access.getter) && setter.equals(access.setter);

    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + getter.hashCode();
        result = 31 * result + setter.hashCode();
        return result;
    }
}
