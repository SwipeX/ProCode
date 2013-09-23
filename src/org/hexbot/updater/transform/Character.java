package org.hexbot.updater.transform;

import org.hexbot.updater.Updater;
import org.hexbot.updater.search.EntryPattern;
import org.hexbot.updater.search.InsnEntry;
import org.hexbot.updater.transform.parent.Container;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.util.Map;

public class Character extends Container {

    public Character(Updater updater) {
        super(updater);
    }

    @Override
    public int getTotalHookCount() {
        return 5;
    }

    @Override
    public ClassNode validate(Map<String, ClassNode> classnodes) {
        for (ClassNode cn : classnodes.values()) {
            if (cn.superName.equals(CLASS_MATCHES.get("Renderable"))) {
                if (cn.fieldCount("[I") == 5 && cn.fieldCount("Z") == 1 && cn.fieldCount("Ljava/lang/String;") == 1) {
                    CLASS_MATCHES.put("Character", cn.name);
                    return cn;
                }
            }
        }
        return null;
    }

    @Override
    public void transform(ClassNode cn) {
        EntryPattern ep = new EntryPattern(new InsnEntry(Opcodes.GETFIELD, "[I"), new InsnEntry(Opcodes.IADD), new InsnEntry(Opcodes.PUTFIELD, "I"),
				new InsnEntry(Opcodes.GETFIELD, "[I"), new InsnEntry(Opcodes.IADD), new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.RETURN));
        ep.find(cn);
        FieldInsnNode x =ep.get(2, FieldInsnNode.class);
        FieldInsnNode y =ep.get(5, FieldInsnNode.class);
        addHook("getX", x.name, x.owner, x.owner, x.desc, -1);
        addHook("getY", y.name, y.owner, y.owner, y.desc, -1);

        EntryPattern ep1 = new EntryPattern(new InsnEntry(Opcodes.ACONST_NULL), new InsnEntry(Opcodes.PUTFIELD, "Ljava/lang/String;"));
        ep1.find(cn);
        FieldInsnNode text =ep1.get(1, FieldInsnNode.class);
        addHook("getAboveText", text.name, text.owner, text.owner, text.desc, -1);

        EntryPattern ep2 = new EntryPattern(new InsnEntry(Opcodes.IF_ICMPNE), new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.GETFIELD, "[I"));
        if (ep2.find(cn)) {
            FieldInsnNode anim =ep2.get(1, FieldInsnNode.class);
            addHook("getAnimation", anim.name, anim.owner, anim.owner, anim.desc, -1);
        }

        EntryPattern ep3 = new EntryPattern(new InsnEntry(Opcodes.RETURN), new InsnEntry(Opcodes.GETFIELD, "I"), new InsnEntry(Opcodes.GETFIELD, "I"), new InsnEntry(Opcodes.GETSTATIC, "[L" + CLASS_MATCHES.get("Npc") + ";"), new InsnEntry(Opcodes.GETFIELD, "I"), new InsnEntry(Opcodes.AALOAD));
        ep3.find(updater.classnodes.get("client"), "(L" + CLASS_MATCHES.get("Character") + ";I)V");
        FieldInsnNode index =ep3.get(1, FieldInsnNode.class);
        addHook("getInteractingIndex", index.name, index.owner, CLASS_MATCHES.get("Character"), index.desc, -1);

        EntryPattern ep4 = new EntryPattern(new InsnEntry(Opcodes.PUTFIELD, "I"), new InsnEntry(Opcodes.RETURN));
        ep4.find(updater.classnodes.get("client"), "(L" + CLASS_MATCHES.get("Character") + ";)V");
        FieldInsnNode orientaion =ep4.get(0, FieldInsnNode.class);
        addHook("getOrientation", orientaion.name, orientaion.owner, CLASS_MATCHES.get("Character"), orientaion.desc, -1);
    }

}
