package org.snorochevskiy.dtomapper;

public class MyMapperClassLoader extends ClassLoader {

    private static class SingletonHolder {
        public static final MyMapperClassLoader instance = new MyMapperClassLoader();
    }

    public static MyMapperClassLoader getInstance()  {
        return SingletonHolder.instance;
    }

    public Class addMapper(String name, byte[] bytes) {
        return defineClass(name, bytes, 0, bytes.length);
    }
}
