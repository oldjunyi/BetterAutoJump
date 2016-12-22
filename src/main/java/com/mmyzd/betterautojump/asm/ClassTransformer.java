package com.mmyzd.betterautojump.asm;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.mmyzd.betterautojump.asm.*;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLRemappingAdapter;

public class ClassTransformer implements IClassTransformer {

	public Map<String, BasePatch> patches = new HashMap<String, BasePatch>();

	public ClassTransformer() {
		load(PatchGuiContorls.class);
		load(PatchEntityPlayerSP.class);
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		ClassReader reader = new ClassReader(bytes);
		ClassNode node = new ClassNode();
		reader.accept(new FMLRemappingAdapter(node), ClassReader.EXPAND_FRAMES);
		BasePatch patch = patches.get(transformedName);
		if (patch == null) return bytes;
		patch.apply(transformedName, node);
		ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
		node.accept(writer);
		return writer.toByteArray();
	}

	private void load(Class<? extends BasePatch> c) {
		try {
			BasePatch patch = c.newInstance();
			if (patch.getTargetClass() != null) {
				patches.put(patch.getTargetClass(), patch);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
