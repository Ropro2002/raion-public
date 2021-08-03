package me.robeart.raion.client.command;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.util.ChatUtils;

/**
 * @author Robeart
 */
public class FriendCommand extends Command {
	
	public FriendCommand() {
		super("Friend", new String[]{"friend"}, "Manage Friends", "friend add/remove [player]");
	}
	
	@Override
	public void call(String[] args) {
		if (args.length == 0) {
			ChatUtils.message("Please specify add/remove and a player!");
			return;
		}
		if (args.length == 1) {
			ChatUtils.message("Please specify a player!");
			return;
		}
		if (args[0].equalsIgnoreCase("add")) {
			if (Raion.INSTANCE.getFriendManager().add(args[1]))
				ChatUtils.message("Added " + args[1] + " to your friendslist");
			else ChatUtils.message(args[1] + "is already in your friendslist");
			
		}
		if (args[0].equalsIgnoreCase("remove")) {
			if (Raion.INSTANCE.getFriendManager().remove(args[1]))
				ChatUtils.message("Removed " + args[1] + " from your friendslist");
			else ChatUtils.message("Can't find " + args[1] + " in your friendslist");
			
		}
	}
	
}
