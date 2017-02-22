package org.snorochevskiy;

import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MapperConfig {

    private Class src;
    private Class dst;

    public MapperConfig(Class src, Class dst) {
        this.src = src;
        this.dst = dst;
    }

    private List<MappedProperty> mappedProperties = new ArrayList<>();

    public Class getSrc() {
        return src;
    }


    public Class getDst() {
        return dst;
    }

    public List<MappedProperty> getMappedProperties() {
        return mappedProperties;
    }

    public static MapperConfig buildMapperConfig(Class src, Class dst) {
        MapperConfig config = new MapperConfig(src, dst);

        Field[] srcFields =  src.getDeclaredFields();
        Field[] dstFields = dst.getDeclaredFields();

        for (Field srcField : srcFields) {
            for (Field dstField : dstFields) {

                String srcFieldName = srcField.getName();
                String dstFieldName = dstField.getName();

                if (srcField.getName().equals(dstField.getName()) && srcField.getType().equals(dstField.getType())) {

                    String getterName = findGetterForFiledName(src, srcField);
                    String setterName = findSetterForFiledName(dst, dstField);

                    if (getterName == null || setterName == null) {
                        continue;
                    }

                    MappedProperty property = new MappedProperty();
                    property.setSrcName(srcField.getName());
                    property.setDstName(dstField.getName());
                    property.setType(Type.getType(srcField.getType()));
                    property.setGetter(getterName);
                    property.setSetter(setterName);
                    config.getMappedProperties().add(property);
                }
            }
        }

        return config;
    }

    public static String findGetterForFiledName(Class cls, Field field) {

        try {
            Method method = cls.getMethod(getterName(field.getName()));
            if (method.getReturnType().equals(field.getType())) {
                return method.getName();
            }
        } catch (NoSuchMethodException e) {
            // method not found
        }

        if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
            try {
                Method method = cls.getMethod(isGetterName(field.getName()));
                if (method.getReturnType().equals(field.getType())) {
                    return method.getName();
                }
            } catch (NoSuchMethodException e) {
                // method not found
            }
        }

        return null;
    }

    public static String findSetterForFiledName(Class cls, Field field) {

        try {
            String supposedSetterName = setterName(field.getName());
            Method method = cls.getMethod(supposedSetterName, field.getType());

            if (method.getParameterCount() == 1 && method.getParameters()[0].getType().equals(field.getType())) {
                return method.getName();
            }
        } catch (NoSuchMethodException e) {
            // method not found
        }

        return null;
    }

    public static String getterName(String field) {
        String capitalizedFirstChar = ("" + field.charAt(0)).toUpperCase();
        return "get" + capitalizedFirstChar + field.substring(1);
    }

    public static String isGetterName(String field) {
        String capitalizedFirstChar = ("" + field.charAt(0)).toUpperCase();
        return "is" + capitalizedFirstChar + field.substring(1);
    }

    public static String setterName(String field) {
        String capitalizedFirstChar = ("" + field.charAt(0)).toUpperCase();
        return "set" + capitalizedFirstChar + field.substring(1);
    }

}
