package com.mmyzd.betterautojump;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

@Mod(modid = BetterAutoJump.MODID, clientSideOnly = true, useMetadata = true)
public class BetterAutoJump {

	public static final String MODID = "betterautojump";

	@Instance(MODID)
	public static BetterAutoJump instance;

	public ConfigManager config;
	public KeyBinding keyToggleWalking;
	public String message;
	public double messageRemainingTicks;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		config = new ConfigManager(evt.getModConfigurationDirectory());
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(config);
		MinecraftForge.EVENT_BUS.register(this);
		keyToggleWalking = new KeyBinding("key.betterautojump.toggle_walking", KeyConflictContext.IN_GAME, Keyboard.KEY_G, "key.categories.betterautojump");
		ClientRegistry.registerKeyBinding(keyToggleWalking);
	}
	
	@SubscribeEvent
	public void onKeyInputEvent(KeyInputEvent event) {
		if (keyToggleWalking.isPressed()) {
			String movingMode = config.movingMode.getString();
			if (movingMode.equals(ConfigManager.MOVING_MODE_WALKING)) {
				movingMode = ConfigManager.MOVING_MODE_SPRINTING;
			} else {
				movingMode = ConfigManager.MOVING_MODE_WALKING;
				Minecraft.getMinecraft().player.setSprinting(false);
			}
			config.keepAutoJumpModeConsistent();
			if (config.autoJumpMode.getString().equals(ConfigManager.AUTO_JUMP_MODE_HOLD)) {
				Minecraft.getMinecraft().gameSettings.setOptionValue(Options.AUTO_JUMP, 1);
			}
			config.movingMode.set(movingMode);
			config.file.save();
			message = "Better Auto Jump: " + StringUtils.capitalize(movingMode);
			messageRemainingTicks = 40;
		}
	}

	@SubscribeEvent
	public void onRenderGameOverlayEventText(RenderGameOverlayEvent.Text event) {
		if (messageRemainingTicks > 0) {
			messageRemainingTicks -= event.getPartialTicks();
			event.getLeft().add(message);
		}
	}

}
