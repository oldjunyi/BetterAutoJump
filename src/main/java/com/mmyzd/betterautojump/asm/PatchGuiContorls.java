package com.mmyzd.betterautojump.asm;

import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.ImmutableMap;

public class PatchGuiContorls extends BasePatch {

	@Override
	public String getTargetClass() {
		return "net.minecraft.client.gui.GuiControls";
	}

	@Override
	public Map<String, String> getMethodPatches() {
		return ImmutableMap.of(
			NameMappings.initGui + ": void()", "patchInitGui",
			NameMappings.actionPerformed + ": void(net.minecraft.client.gui.GuiButton)", "patchActionPerformed"
		);
	}

	public AbstractInsnNode[] patchInitGui(AbstractInsnNode node) {
		if (node.getOpcode() == Opcodes.RETURN) {
			return new AbstractInsnNode[] {
				new VarInsnNode(Opcodes.ALOAD, 0),
				new FieldInsnNode(Opcodes.GETFIELD,
						ASMHelper.getIntlFromType("net.minecraft.client.gui.GuiControls"), NameMappings.buttonList,
						ASMHelper.getDescFromType("java.util.List")),
				new MethodInsnNode(Opcodes.INVOKESTATIC,
						ASMHelper.getIntlFromType("com.mmyzd.betterautojump.asm.MethodHook"), "addButtonToGuiControls",
						ASMHelper.getDescFromType("void(java.util.List)"), false),
				node
			};
		}
		return null;
	}

	private int stateActionPerformed = 0;
	public AbstractInsnNode[] patchActionPerformed(AbstractInsnNode node) {
		if (stateActionPerformed == 0) {
			stateActionPerformed++;
			return new AbstractInsnNode[] {
				new VarInsnNode(Opcodes.ALOAD, 1),
				new MethodInsnNode(Opcodes.INVOKESTATIC,
						ASMHelper.getIntlFromType("com.mmyzd.betterautojump.asm.MethodHook"), "handleActionInGuiControls",
						ASMHelper.getDescFromType("void(net.minecraft.client.gui.GuiButton)"), false),				
				node
			};
		}
		return null;
	}

}
