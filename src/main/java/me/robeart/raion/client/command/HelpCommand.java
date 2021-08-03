package me.robeart.raion.client.command;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.util.ChatUtils;

public class HelpCommand extends Command {
	
	public HelpCommand() {
		super("Help", new String[]{"?", "wtf"}, "show the help screen", "help");
	}
	
	@Override
	public void call(String[] args) {
		StringBuilder text = new StringBuilder("Avalaible commands:\n");
		for (Command c : Raion.INSTANCE.getCommandManager().getCommandList()) {
			text.append(c.getName())
				.append(": ")
				.append(c.getUsage())
				.append("\n");
		}
		ChatUtils.message(text.toString());
	}
	
}
