package me.robeart.raion.client.command;

import me.robeart.raion.client.Raion;

/**
 * @author Robeart
 */
public class SaveCommand extends Command {
	
	public SaveCommand() {
		super("Save", new String[]{"safe"}, "Save the config", "save");
	}
	
	
	@Override
	public void call(String[] args) {
		Raion.INSTANCE.getConfigManager().saveAll();
	}
	
}
