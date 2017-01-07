package com.mmyzd.betterautojump;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = BetterAutoJump.MODID, clientSideOnly = true, useMetadata = true)
public class BetterAutoJump {

	public static final String MODID = "betterautojump";

	@Instance(MODID)
	public static BetterAutoJump instance;

	public ConfigManager config;
	public KeyBinding keyToggleWalking;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		config = new ConfigManager(evt.getModConfigurationDirectory());
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(config);
		keyToggleWalking = new KeyBinding("key.betterautojump.toggle_walking", Keyboard.KEY_G, "key.categories.betterautojump");
		ClientRegistry.registerKeyBinding(keyToggleWalking);
	}

}
