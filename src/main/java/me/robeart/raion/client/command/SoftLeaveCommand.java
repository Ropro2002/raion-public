package me.robeart.raion.client.command;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;

/**
 * @author cookiedragon234 18/Jan/2020
 */
public class SoftLeaveCommand extends Command {
	public SoftLeaveCommand() {
		super("softleave", "Leave without closing the connection", "softleave {unload}");
	}
	
	@Override
	public void call(String[] args) {
		if (args.length >= 1 && args[0].equals("unload")) {
			mc.loadWorld(null);
		}
		
		mc.currentScreen = new GuiMultiplayer(new GuiMainMenu());
	}
}
