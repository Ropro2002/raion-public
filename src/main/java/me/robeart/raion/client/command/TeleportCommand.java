package me.robeart.raion.client.command;

import me.robeart.raion.client.util.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class TeleportCommand extends Command {
	
	public TeleportCommand() {
		super("Teleport", new String[]{"tp"}, "Teleport to where you want to go ", "tp [y], tp [x] [z], tp [x] [y] [z]");
	}
	
	@Override
	public void call(String[] args) {
		if (args.length == 0) {
			ChatUtils.message("Invalid arguments");
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		if (args.length == 1) {
			player.setPosition(player.posX, player.posY + Double.parseDouble(args[0]), player.posZ);
		}
		if (args.length == 2)
			player.setPosition(player.posX + Double.parseDouble(args[0]), player.posY, player.posZ + Double.parseDouble(args[1]));
		if (args.length == 3)
			player.setPosition(player.posX + Double.parseDouble(args[0]), player.posY + Double.parseDouble(args[1]), player.posZ + Double
				.parseDouble(args[2]));
		
	}
}
