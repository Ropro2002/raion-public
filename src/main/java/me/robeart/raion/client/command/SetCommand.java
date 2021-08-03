package me.robeart.raion.client.command;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.ChatUtils;
import me.robeart.raion.client.value.*;

/**
 * @author cats
 * Quick set command to make working with smaller or more exact values easier
 */
public class SetCommand extends Command {
	
	public SetCommand() {
		super("Set", new String[]{"s"}, "Sets a value within a module", "set [module] <setting> <value>");
	}
	
	
	public void call(String[] args) {
		if (args.length < 3) {
			ChatUtils.message(this.getUsage());
			return;
		}
		Module module = Raion.INSTANCE.getModuleManager().getModule(args[0]);
		if (module == null) {
			ChatUtils.message("Can't find module " + args[0]);
			return;
		}
		if (module.getValues().isEmpty()) {
			ChatUtils.message("Module " + args[0] + " has no settings");
			return;
		}
		for (Value<?> value : module.getValues()) {
			String name = value.getName().replace(" ", "");
			if (name.equalsIgnoreCase(args[1])) {
				if (value instanceof BooleanValue) {
					((BooleanValue) value).setValue(Boolean.parseBoolean(args[2]));
					ChatUtils.message("Set " + args[1] + " in " + args[0] + " to " + args[2]);
					return;
				}
				if (value instanceof DoubleValue) {
					((DoubleValue) value).setValue(Double.parseDouble(args[2]));
					ChatUtils.message("Set " + args[1] + " in " + args[0] + " to " + args[2]);
					return;
				}
				if (value instanceof FloatValue) {
					((FloatValue) value).setValue(Float.parseFloat(args[2]));
					ChatUtils.message("Set " + args[1] + " in " + args[0] + " to " + args[2]);
					return;
				}
				if (value instanceof IntValue) {
					((IntValue) value).setValue(Integer.parseInt(args[2]));
					ChatUtils.message("Set " + args[1] + " in " + args[0] + " to " + args[2]);
					return;
				}
				if (value instanceof ListValue) {
					((ListValue) value).setValue(args[2]);
					ChatUtils.message("Set " + args[1] + " in " + args[0] + " to " + args[2]);
					return;
				}
				if (value instanceof StringValue) {
					((StringValue) value).setValue(args[2]);
					ChatUtils.message("Set " + args[1] + " in " + args[0] + " to " + args[2]);
					return;
				}
			}
		}
		
		ChatUtils.message("No such setting!");
	}
}
