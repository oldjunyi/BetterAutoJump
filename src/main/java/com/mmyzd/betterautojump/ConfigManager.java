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

	private String negativeMode = ConfigManager.MODE_HOLD;

	public Property mode;
	public static final String MODE_ON = "on";
	public static final String MODE_HOLD = "hold";
	public static final String MODE_OFF = "off";

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

		String[] modeValidValues = new String[] { MODE_ON, MODE_HOLD, MODE_OFF };
		String modeDefault = MODE_HOLD;
		String modeComment = "Auto-jump mode.";
		modeComment += " Available options: [" + StringUtils.join(modeValidValues, ", ") + "].";
		modeComment += " Default: "+ modeDefault + ".";
		mode = file.get("general", "mode", modeDefault, modeComment, modeValidValues);
		mode.set(mode.getString());
		if (mode.getString().equals(MODE_ON)) {
			Minecraft.getMinecraft().gameSettings.autoJump = true;
		} else {
			Minecraft.getMinecraft().gameSettings.autoJump = false;
			negativeMode = mode.getString();
		}

		file.save();
	}
	
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		keepAutoJumpModeConsistent();
	}
	
	public void keepAutoJumpModeConsistent() {
		if (Minecraft.getMinecraft().gameSettings.autoJump) {
			if (!mode.getString().equals(MODE_ON)) {
				mode.set(MODE_ON);
				file.save();
			}
		} else {
			if (mode.getString().equals(MODE_ON)) {
				mode.set(negativeMode);
				file.save();
			}
		}
	}

	public void toggleMode() {
		GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
		if (mode.getString().equals(MODE_ON)) {
			mode.set(negativeMode = MODE_HOLD);
			gameSettings.setOptionValue(Options.AUTO_JUMP, 1);
		} else if (mode.getString().equals(MODE_HOLD)) {
			mode.set(negativeMode = MODE_OFF);
		} else if (mode.getString().equals(MODE_OFF)) {
			mode.set(MODE_ON);
			gameSettings.setOptionValue(Options.AUTO_JUMP, 1);
		} else {
			return;
		}
		file.save();
	}

	public String getAutoJumpButtonDisplayString() {
		GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
		String displayValue;
		if (mode.getString().equals(MODE_HOLD)) {
			displayValue = I18n.format("options.betterautojump." + mode.getString());
		} else {
			displayValue = I18n.format("options." + mode.getString());
		}
		String displayKey = I18n.format(Options.AUTO_JUMP.getEnumString());
		return displayKey + ": " + displayValue;
	}

}
