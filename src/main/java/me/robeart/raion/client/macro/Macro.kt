package me.robeart.raion.client.macro

import me.robeart.raion.client.util.ChatUtils
import me.robeart.raion.client.util.Key
import net.minecraft.client.Minecraft

/**
 * @author cats
 */
data class Macro(var bind: Key, var commands: String) {
	fun run() {
		ChatUtils.message("Executed macro for key $bind")
		for (string in commands.split(";")) {
			if (!string.isBlank()) {
				Minecraft.getMinecraft().player.sendChatMessage(string)
			}
		}
	}
}
