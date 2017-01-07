package com.mmyzd.betterautojump;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigManager {

	public Configuration file;

	private String negativeMode = ConfigManager.AUTO_JUMP_MODE_HOLD;

	public Property autoJumpMode;
	public static final String AUTO_JUMP_MODE_ON = "on";
	public static final String AUTO_JUMP_MODE_HOLD = "hold";
	public static final String AUTO_JUMO_MODE_OFF = "off";
	
	public Property movingMode;
	public static final String MOVING_MODE_WALKING = "walking";
	public static final String MOVING_MODE_SPRINTING = "sprinting";

	public ConfigManager(File configDir) {
		file = new Configuration(new File(configDir, "BetterAutoJump.cfg"));
		reload();
	}

	@SubscribeEvent
	public void onConfigurationChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(BetterAutoJump.MODID)) {
			file.save();
		}
	}

	void reload() {
		file.load();

		String[] autoJumpModeOptions = new String[] { AUTO_JUMP_MODE_ON, AUTO_JUMP_MODE_HOLD, AUTO_JUMO_MODE_OFF };
		String autoJumpModeDefaultValue = AUTO_JUMP_MODE_HOLD;
		String autoJumpModeComment = "The auto-jump mode.";
		autoJumpModeComment += " Available options: [" + StringUtils.join(autoJumpModeOptions, ", ") + "].";
		autoJumpModeComment += " Default: " + autoJumpModeDefaultValue + ".";
		autoJumpMode = file.get("general", "autoJumpMode", autoJumpModeDefaultValue, autoJumpModeComment, autoJumpModeOptions);
		autoJumpMode.set(autoJumpMode.getString());
		if (autoJumpMode.getString().equals(AUTO_JUMP_MODE_ON)) {
			Minecraft.getMinecraft().gameSettings.autoJump = true;
		} else {
			Minecraft.getMinecraft().gameSettings.autoJump = false;
			negativeMode = autoJumpMode.getString();
		}
		
		String[] movingModeOptions = new String[] { MOVING_MODE_WALKING, MOVING_MODE_SPRINTING };
		String movingModeDefaultValue = MOVING_MODE_WALKING;
		String movingModeComment = "The default moving mode.";
		movingModeComment += " Available options: [" + StringUtils.join(movingModeOptions, ", ") + "].";
		movingModeComment += " Default: " + movingModeDefaultValue + ".";
		movingMode = file.get("general", "movingMode", movingModeDefaultValue, movingModeComment, movingModeOptions);
		movingMode.set(movingMode.getString());

		file.save();
	}

	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		keepAutoJumpModeConsistent();
	}

	public void keepAutoJumpModeConsistent() {
		if (Minecraft.getMinecraft().gameSettings.autoJump) {
			if (!autoJumpMode.getString().equals(AUTO_JUMP_MODE_ON)) {
				autoJumpMode.set(AUTO_JUMP_MODE_ON);
				file.save();
			}
		} else {
			if (autoJumpMode.getString().equals(AUTO_JUMP_MODE_ON)) {
				autoJumpMode.set(negativeMode);
				file.save();
			}
		}
	}

	public void toggleAutoJumpMode() {
		GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
		if (autoJumpMode.getString().equals(AUTO_JUMP_MODE_ON)) {
			autoJumpMode.set(negativeMode = AUTO_JUMP_MODE_HOLD);
			gameSettings.setOptionValue(Options.AUTO_JUMP, 1);
		} else if (autoJumpMode.getString().equals(AUTO_JUMP_MODE_HOLD)) {
			autoJumpMode.set(negativeMode = AUTO_JUMO_MODE_OFF);
		} else if (autoJumpMode.getString().equals(AUTO_JUMO_MODE_OFF)) {
			autoJumpMode.set(AUTO_JUMP_MODE_ON);
			gameSettings.setOptionValue(Options.AUTO_JUMP, 1);
		} else {
			return;
		}
		file.save();
	}

	public String getAutoJumpButtonDisplayString() {
		GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
		String displayValue;
		if (autoJumpMode.getString().equals(AUTO_JUMP_MODE_HOLD)) {
			displayValue = I18n.format("options.betterautojump." + autoJumpMode.getString());
		} else {
			displayValue = I18n.format("options." + autoJumpMode.getString());
		}
		String displayKey = I18n.format(Options.AUTO_JUMP.getEnumString());
		return displayKey + ": " + displayValue;
	}

}
