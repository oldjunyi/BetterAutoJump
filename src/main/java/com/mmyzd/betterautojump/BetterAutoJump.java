package com.mmyzd.betterautojump;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
	modid = BetterAutoJump.MODID,
	clientSideOnly = true,
	useMetadata = true,
	acceptedMinecraftVersions = "[1.10,1.11]"
)
public class BetterAutoJump {

	public static final String MODID = "betterautojump";

	@Instance(MODID)
	public static BetterAutoJump instance;

	public ConfigManager config;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		config = new ConfigManager(evt.getModConfigurationDirectory());
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(config);
	}

}
