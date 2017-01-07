package com.mmyzd.betterautojump.asm;

import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import com.google.common.collect.ImmutableMap;

public class PatchEntityPlayerSP extends BasePatch {

	@Override
	public String getTargetClass() {
		return "net.minecraft.client.entity.EntityPlayerSP";
	}

	@Override
	public Map<String, String> getMethodPatches() {
		return ImmutableMap.of(NameMappings.updateAutoJump + ": void(float, float)", "patchUpdateAutoJump",
				NameMappings.onUpdateWalkingPlayer + ": void()", "patchOnUpdateWalkingPlayer",
				NameMappings.onLivingUpdate + ": void()", "patchOnLivingUpdate");
	}

	private int stateUpdateAutoJump = 0;

	public AbstractInsnNode[] patchUpdateAutoJump(AbstractInsnNode node) {
		if (stateUpdateAutoJump == 0) {
			if (node instanceof MethodInsnNode) {
				MethodInsnNode methodInsnNode = (MethodInsnNode) node;
				if (methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
						&& methodInsnNode.owner
								.equals(ASMHelper.getIntlFromType("net.minecraft.client.entity.EntityPlayerSP"))
						&& methodInsnNode.name.equals(NameMappings.isSneaking)
						&& methodInsnNode.desc.equals(ASMHelper.getDescFromType("boolean()"))) {
					stateUpdateAutoJump++;
					return new AbstractInsnNode[] { new MethodInsnNode(Opcodes.INVOKESTATIC,
							ASMHelper.getIntlFromType("com.mmyzd.betterautojump.asm.MethodHook"), "disallowAutoJump",
							ASMHelper.getDescFromType("boolean(net.minecraft.client.entity.EntityPlayerSP)"), false), };
				}
			}
		}
		return null;
	}

	private int stateOnUpdateWalkingPlayer = 0;

	public AbstractInsnNode[] patchOnUpdateWalkingPlayer(AbstractInsnNode node) {
		if (stateOnUpdateWalkingPlayer == 0) {
			if (node instanceof FieldInsnNode) {
				FieldInsnNode fieldInsnNode = (FieldInsnNode) node;
				if (fieldInsnNode.getOpcode() == Opcodes.PUTFIELD
						&& fieldInsnNode.owner
								.equals(ASMHelper.getIntlFromType("net.minecraft.client.entity.EntityPlayerSP"))
						&& fieldInsnNode.name.equals(NameMappings.autoJumpEnabled)
						&& fieldInsnNode.desc.equals(ASMHelper.getDescFromType("boolean"))) {
					stateOnUpdateWalkingPlayer++;
					return new AbstractInsnNode[] { new InsnNode(Opcodes.POP),
							new MethodInsnNode(Opcodes.INVOKESTATIC,
									ASMHelper.getIntlFromType("com.mmyzd.betterautojump.asm.MethodHook"),
									"isAutoJumpEnabled", ASMHelper.getDescFromType("boolean()"), false),
							node };
				}
			}
		}
		return null;
	}

	private int stateOnLivingUpdate = 0;

	public AbstractInsnNode[] patchOnLivingUpdate(AbstractInsnNode node) {
		if (stateOnLivingUpdate == 0 || stateOnLivingUpdate == 3) {
			if (node instanceof FieldInsnNode) {
				FieldInsnNode fieldInsnNode = (FieldInsnNode) node;
				if (fieldInsnNode.getOpcode() == Opcodes.GETSTATIC
						&& fieldInsnNode.owner.equals(ASMHelper.getIntlFromType("net.minecraft.init.MobEffects"))
						&& fieldInsnNode.name.equals(NameMappings.BLINDNESS)
						&& fieldInsnNode.desc.equals(ASMHelper.getDescFromType("net.minecraft.potion.Potion"))) {
					stateOnLivingUpdate++;
					return null;
				}
			}
		} else if (stateOnLivingUpdate == 1) {
			if (node.getOpcode() == Opcodes.IFGT) {
				stateOnLivingUpdate++;
				return null;
			}
		} else if (stateOnLivingUpdate == 2 || stateOnLivingUpdate == 5) {
			if (node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode methodInsnNode = (MethodInsnNode) node;
				if (methodInsnNode.owner.equals(ASMHelper.getIntlFromType("net.minecraft.client.settings.KeyBinding"))
						&& methodInsnNode.name.equals(NameMappings.isKeyDown)
						&& methodInsnNode.desc.equals(ASMHelper.getDescFromType("boolean()"))) {
					stateOnLivingUpdate++;
					return new AbstractInsnNode[] { node,
							new MethodInsnNode(Opcodes.INVOKESTATIC,
									ASMHelper.getIntlFromType("com.mmyzd.betterautojump.asm.MethodHook"),
									"isMovingModeEqualToSprinting", ASMHelper.getDescFromType("boolean()"), false),
							new InsnNode(Opcodes.IXOR) };
				}
			}
		} else if (stateOnLivingUpdate == 4) {
			if (node.getOpcode() == Opcodes.IFNE) {
				stateOnLivingUpdate++;
				return null;
			}
		} else if (stateOnLivingUpdate == 6) {
			if (node.getOpcode() == Opcodes.LDC) {
				stateOnLivingUpdate++;
				return null;
			}
		} else if (stateOnLivingUpdate == 7) {
			if (node.getOpcode() == Opcodes.IFLT) {
				stateOnLivingUpdate++;
				JumpInsnNode jumpInsnNode = (JumpInsnNode) node;
				return new AbstractInsnNode[] { node,
						new MethodInsnNode(Opcodes.INVOKESTATIC,
								ASMHelper.getIntlFromType("com.mmyzd.betterautojump.asm.MethodHook"), "isForcedToWalk",
								ASMHelper.getDescFromType("boolean()"), false),
						new JumpInsnNode(Opcodes.IFNE, jumpInsnNode.label) };
			}
		}
		return null;
	}

}
