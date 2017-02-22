package org.snorochevskiy.dtomapper;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;

/**
 * This class generates a mapper that maps field with same names/types between entities
 */
public class MapperGenerator {

    public <T1, T2> IMapper<T1, T2> generate(Class<T1> t1, Class<T2> t2) throws IllegalAccessException, InstantiationException {

        MapperConfig config = MapperConfig.buildMapperConfig(t1, t2);

        String entityClassName= t1.getSimpleName();
        String dtoClassName = t2.getSimpleName();
        String mapperClassName = "generated/mapper/" + entityClassName + "To" + dtoClassName;

        ClassWriter cw = new ClassWriter(
                ClassWriter.COMPUTE_FRAMES // Slows down the generating, but takes the burden of manual calling to visitFrame
        );
        TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out));
        CheckClassAdapter cv = new CheckClassAdapter(tcv);

        cw.visit(Opcodes.V1_5, // Target JVM bytecode version. Change to '52' later
                Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, // class modifiers
                mapperClassName,
                null,
                "java/lang/Object", // Base class
                new String[] { Type.getInternalName(IMapper.class)} // "org/snorochevskiy/IMapper"
                );

        generateConstructor(cw);
        generateMapMethod(cw, config);
        generateMapFromInterface(cw, mapperClassName, config);

        cw.visitEnd();

        byte[] bytes = cw.toByteArray();

        String classToRegister = mapperClassName.replaceAll("\\/", ".");
        Class<IMapper<T1, T2>> registeredMapperClass = MyMapperClassLoader.getInstance().addMapper(classToRegister, bytes);
        return registeredMapperClass.newInstance();
    }

    private void generateConstructor(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    /**
     * Method is based on org.objectweb.asm.util.ASMifier output
     * @param cw
     * @param config
     */
    private void generateMapMethod(ClassWriter cw, MapperConfig config) {

        String descriptor = "(" + Type.getDescriptor(config.getSrc()) + ")" + Type.getDescriptor(config.getDst());

        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "map", descriptor, null, null);

        // Start method code generation
        mv.visitCode();

        // Creating destination object
        mv.visitTypeInsn(Opcodes.NEW, Type.getInternalName(config.getDst()));
        mv.visitInsn(Opcodes.DUP);

        // Constructor
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(config.getDst()), "<init>", "()V", false);
        mv.visitVarInsn(Opcodes.ASTORE, 2);

        // Generate mapping calls
        for (MappedProperty property : config.getMappedProperties()) {
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(config.getSrc()), property.getGetter(), "()" + property.getType().getDescriptor(), false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(config.getDst()), property.getSetter(), "(" + property.getType().getDescriptor() + ")V", false);
        }

        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(2, 3);
        mv.visitEnd();
    }

    private void generateMapFromInterface(ClassWriter cw, String mapperClassName, MapperConfig config) {
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_BRIDGE + Opcodes.ACC_SYNTHETIC, "map", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(config.getSrc()));
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, mapperClassName, "map", "(" + Type.getDescriptor(config.getSrc())+ ")" + Type.getDescriptor(config.getDst()), false);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

}
