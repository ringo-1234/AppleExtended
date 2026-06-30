package jp.apple.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class WorldClientTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!"net.minecraft.client.multiplayer.WorldClient".equals(transformedName)) {
            return basicClass;
        }

        ClassNode classNode = new ClassNode();
        new ClassReader(basicClass).accept(classNode, 0);

        MethodNode method = new MethodNode(
                ACC_PUBLIC,
                "func_73045_a",
                "(Ljava/lang/Object;)Lnet/minecraft/entity/Entity;",
                null, null
        );
        InsnList insns = method.instructions;
        insns.add(new VarInsnNode(ALOAD, 0));
        insns.add(new VarInsnNode(ALOAD, 1));
        insns.add(new MethodInsnNode(INVOKEVIRTUAL,
                "java/lang/Object", "toString", "()Ljava/lang/String;", false));
        insns.add(new MethodInsnNode(INVOKESTATIC,
                "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false));
        insns.add(new MethodInsnNode(INVOKEVIRTUAL,
                "net/minecraft/client/multiplayer/WorldClient",
                "func_73045_a", "(I)Lnet/minecraft/entity/Entity;", false));
        insns.add(new InsnNode(ARETURN));
        method.maxStack = 2;
        method.maxLocals = 2;

        classNode.methods.add(method);

        ClassWriter writer = new ClassWriter(0);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
