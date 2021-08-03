package me.robeart.raion.client.command;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.util.ChatUtils;

public class PrefixCommand extends Command {
	
	public PrefixCommand() {
		super("Prefix", "Set the prefix", "prefix [prefix]");
	}
	
	@Override
	public void call(String[] args) {
		if (args.length == 0) {
			ChatUtils.message("Please specify what you would like as prefix!");
			return;
		}
		Raion.INSTANCE.getCommandManager().setPrefix(args[0]);
		ChatUtils.message("Prefix has been changed to: " + args[0]);
	}
}
