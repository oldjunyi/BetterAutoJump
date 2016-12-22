package com.mmyzd.betterautojump.asm;

import java.lang.reflect.Method;
import java.util.ListIterator;
import java.util.Map;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public class BasePatch {

	public String getTargetClass() {
		return null;
	}

	public Map<String, String> getMethodPatches() {
		return null;
	}

	public void apply(String className, ClassNode cn) {
		Map<String, String> patches = getMethodPatches();
		if (patches == null) return;
		for (MethodNode mn: cn.methods) {
			String typedName = mn.name + ": " + ASMHelper.getTypeFromDesc(mn.desc);
			String worker = patches.get(typedName);
			if (worker != null) {
				try {
					Method m = getClass().getMethod(worker, AbstractInsnNode.class);
					InsnList instructions = new InsnList();
					for (ListIterator<AbstractInsnNode> i = mn.instructions.iterator(); i.hasNext(); ) {
						AbstractInsnNode node = i.next();
						AbstractInsnNode[] replacements = (AbstractInsnNode[])m.invoke(this, node);
						if (replacements == null) {
							replacements = new AbstractInsnNode[] {node};
						}
						for (AbstractInsnNode replacement: replacements) {
							instructions.add(replacement);
						}
					}
					mn.instructions.clear();
					mn.instructions.add(instructions);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
