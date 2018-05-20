package com.mmyzd.betterautojump.asm;

import java.util.List;

import com.mmyzd.betterautojump.BetterAutoJump;
import com.mmyzd.betterautojump.ConfigManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.settings.GameSettings.Options;

public class MethodHook {

	public static int autoJumpButtonId = -1;

	public static void addButtonToGuiControls(List<GuiButton> buttonList) {
		ConfigManager config = BetterAutoJump.instance.config;
		for (GuiButton button : buttonList) {
			if (button instanceof GuiOptionButton) {
				GuiOptionButton optionButton = (GuiOptionButton) button;
				if (optionButton.getOption() == Options.AUTO_JUMP) {
					autoJumpButtonId = button.id;
					buttonList.add(new GuiButton(autoJumpButtonId, button.x, button.y, button.width, button.height,
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
			config.toggleAutoJumpMode();
		}
		button.displayString = config.getAutoJumpButtonDisplayString();
	}

	public static boolean disallowAutoJump(EntityPlayerSP player) {
		ConfigManager config = BetterAutoJump.instance.config;
		if (config.autoJumpMode.getString().equals(ConfigManager.AUTO_JUMP_MODE_HOLD)) {
			boolean isSprintKeyDown = Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown();
			boolean isSprintingMode = isMovingModeEqualToSprinting();
			return isSprintingMode == isSprintKeyDown;
		}
		return player.isSneaking();
	}

	public static boolean isAutoJumpEnabled() {
		ConfigManager config = BetterAutoJump.instance.config;
		config.keepAutoJumpModeConsistent();
		return Minecraft.getMinecraft().gameSettings.autoJump
				|| config.autoJumpMode.getString().equals(ConfigManager.AUTO_JUMP_MODE_HOLD);
	}

	public static boolean isMovingModeEqualToSprinting() {
		ConfigManager config = BetterAutoJump.instance.config;
		return config.movingMode.getString().equals(ConfigManager.MOVING_MODE_SPRINTING);
	}

	public static boolean isForcedToWalk() {
		return isMovingModeEqualToSprinting() && Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown();
	}

}
