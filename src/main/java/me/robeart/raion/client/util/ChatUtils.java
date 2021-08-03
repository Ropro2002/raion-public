package me.robeart.raion.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ChatUtils {
	
	public static void component(ITextComponent component) {
		Minecraft.getMinecraft().ingameGUI.getChatGUI()
			.printChatMessage(new TextComponentString("\u00a78[\u00A79Raion\u00a78]\u00a7r ").appendSibling(component));
	}
	
	public static void message(String message) {
		component(new TextComponentString(message));
	}
	
	public static void warning(String message) {
		message("\u00a7c[\u00a76\u00a7lWARNING\u00a7c]\u00a7r" + message);
	}
	
	public static void error(String message) {
		message("\u00a7c[\u00a74\u00a7lERROR\u00a7c]\u00a7r " + message);
	}
}
