package org.snorochevskiy.dtomapper;


import org.objectweb.asm.Type;

public class MappedProperty {

    private String srcName;
    private String dstName;

    private String getter;
    private String setter;

    private Type type;

    public String getSrcName() {
        return srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public String getDstName() {
        return dstName;
    }

    public void setDstName(String dstName) {
        this.dstName = dstName;
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
}
