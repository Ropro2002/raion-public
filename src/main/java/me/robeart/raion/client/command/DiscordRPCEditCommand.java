package me.robeart.raion.client.command;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.gui.discordrpc.DiscordPrecenceChangerGui;
import me.robeart.raion.client.module.misc.DiscordRPCModule;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author cookiedragon234 11/Nov/2019
 */
public class DiscordRPCEditCommand extends Command {
	public DiscordRPCEditCommand() {
		super("DiscordRPCEdit", new String[]{"rpc", "discord"}, "Edit Discord RPC", "rpc");
	}
	
	@Override
	public void call(String[] args) {
		/**
		 * when you send a chat message the the {@link net.minecraft.client.gui.GuiChat} automatically sets the
		 * guiscreen to null in order to close the chat screen, and with commands opening a gui will occur
		 * before the guichat closes the gui, if that makes sense.
		 * Therefore we need to schedule a task to occur next game tick
		 *
		 * @see {@link net.minecraft.client.gui.GuiChat#keyTyped(char, int)} (line 132)
		 */
		Raion.INSTANCE.getEventManager().addEventListener(this);
	}
	
	@Listener
	private void onUpdate(OnUpdateEvent event) {
		mc.addScheduledTask(() ->
			{
				DiscordRPCModule.RpcSettings rpcSettings = DiscordRPCModule.getRpcSettings();
				mc.displayGuiScreen(new DiscordPrecenceChangerGui(rpcSettings));
			}
		);
		Raion.INSTANCE.getEventManager().removeEventListener(this);
	}
}
