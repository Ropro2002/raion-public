package me.robeart.raion.client.command;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.ChatUtils;
import org.lwjgl.input.Keyboard;

/**
 * @author Robeart
 */
public class BindCommand extends Command {
	
	public BindCommand() {
		super("Bind", new String[]{"b", "bd"}, "Bind a module", "bind [module] {key}");
	}
	
	@Override
	public void call(String[] args) {
		if (args.length < 2) {
			ChatUtils.message("Please specify a module/key!");
			return;
		}
		Module module = Raion.INSTANCE.getModuleManager().getModule(args[0]);
		if (module == null) {
			ChatUtils.message("Can't find module " + args[0]);
			return;
		}
		module.setBind(Keyboard.getKeyName(Keyboard.getKeyIndex(args[1].toUpperCase())));
		ChatUtils.message("Set keybind of " + module.getName() + " to " + module.getBind());
	}
}
