package me.robeart.raion.client.command;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.ChatUtils;

public class ToggleCommand extends Command {
	
	public ToggleCommand() {
		super("Toggle", new String[]{"t", "tl"}, "Toggle a module", "toggle [module], toggle [module] on/off");
	}
	
	@Override
	public void call(String[] args) {
		if (args.length == 0) {
			ChatUtils.message("Please specify a module!");
			return;
		}
		else {
			Module module = Raion.INSTANCE.getModuleManager().getModule(args[0]);
			if (module == null) {
				ChatUtils.message("Can't find module " + args[0]);
				return;
			}
			if (args.length >= 2) {
				if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("off")) {
					boolean value = args[1].equalsIgnoreCase("on");
					module.setState(value);
				}
				else {
					ChatUtils.message("Invalid argument " + args[1]);
				}
			}
			else module.toggle();
			String state = module.getState() ? "\u00a7aenabled" : "\u00a7cdisabled";
			ChatUtils.message(module.getName() + " has been " + state);
		}
	}
}
