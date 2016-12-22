package com.mmyzd.betterautojump.asm;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.mmyzd.betterautojump.BetterAutoJump;
import com.mmyzd.betterautojump.ConfigManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraftforge.common.config.Property;

public class MethodHook {

	public static int autoJumpButtonId = -1;

	public static void addButtonToGuiControls(List<GuiButton> buttonList) {
		ConfigManager config = BetterAutoJump.instance.config;
		for (GuiButton button : buttonList) {
			if (button instanceof GuiOptionButton) {
				GuiOptionButton optionButton = (GuiOptionButton) button;
				if (optionButton.returnEnumOptions() == Options.AUTO_JUMP) {
					autoJumpButtonId = button.id;
					buttonList.add(new GuiButton(
							autoJumpButtonId,
							button.xPosition, button.yPosition,
							button.width, button.height,
							config.getAutoJumpButtonDisplayString()));
					buttonList.remove(button);
					break;
				}
			}
		}
	}

	public static void handleActionInGuiControls(GuiButton button) {
		ConfigManager config = BetterAutoJump.instance.config;
		if (button.id == autoJumpButtonId) {
			config.toggleMode();
		}
		button.displayString = config.getAutoJumpButtonDisplayString();
	}

	public static boolean disallowAutoJump(EntityPlayerSP player) {
		ConfigManager config = BetterAutoJump.instance.config;
		if (config.mode.getString().equals(ConfigManager.MODE_HOLD)) {
			return !Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown();
		}
		return player.isSneaking();
	}

	public static boolean isAutoJumpEnabled() {
		ConfigManager config = BetterAutoJump.instance.config;
		config.keepAutoJumpModeConsistent();
		return Minecraft.getMinecraft().gameSettings.autoJump ||
				config.mode.getString().equals(ConfigManager.MODE_HOLD);
	}

}
