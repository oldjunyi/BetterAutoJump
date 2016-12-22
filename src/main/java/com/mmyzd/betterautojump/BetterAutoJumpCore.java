package com.mmyzd.betterautojump;

import java.util.Map;

import com.mmyzd.betterautojump.asm.ClassTransformer;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class BetterAutoJumpCore implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { ClassTransformer.class.getName() };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
