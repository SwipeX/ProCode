package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

public class Player extends Container {

    public Player(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 0;
    }

    @Override
    public ClassNode validate(Map<String, ClassNode> classnodes) {
        for (ClassNode cn : classnodes.values()) {
            if (cn.superName.equals(CLASS_MATCHES.get("Character"))) {
                if (cn.fieldCount("Ljava/lang/String;") == 1 && cn.fieldCount("Z") == 1 && cn.getAbnormalFieldCount() == 2) {
                    CLASS_MATCHES.put("Player", cn.name);
                    return cn;
                }
            }
        }
        return null;
    }

    @Override
    public void transform(ClassNode cn) {
        EntryPattern ep = new EntryPattern(new InsnEntry[]{new InsnEntry(Opcodes.ALOAD), new InsnEntry(Opcodes.GETFIELD, "Ljava/lang/String;"), new InsnEntry(Opcodes.PUTSTATIC, "Ljava/lang/String;")});
        for (MethodNode mn : cn.methods) {
            ep.find(mn);
            FieldInsnNode name = (FieldInsnNode) ep.get(1).getInstance();
            addHook("getName", name.name, name.owner, name.owner, name.desc, -1);
            break;
        }
    }
}
