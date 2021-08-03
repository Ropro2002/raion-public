package me.robeart.raion.client.managers;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.network.PacketReceiveEvent;
import me.robeart.raion.client.events.events.network.PlayerJoinEvent;
import me.robeart.raion.client.events.events.network.PlayerLeaveEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author Robeart
 */
public class ConnectionManager {
	
	
	public ConnectionManager() {
		Raion.INSTANCE.getEventManager().addEventListener(this);
	}
	
	@Listener
	public void receivePacket(PacketReceiveEvent event) {
		if (event.getStage() == EventStageable.EventStage.PRE) {
			if (event.getPacket() instanceof SPacketPlayerListItem) {
				SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();
				Minecraft mc = Minecraft.getMinecraft();
				if (mc.player != null) {
					if (packet.getAction() == SPacketPlayerListItem.Action.ADD_PLAYER) {
						for (SPacketPlayerListItem.AddPlayerData playerData : packet.getEntries()) {
							if (!playerData.getProfile()
								.getId()
								.toString()
								.equals(mc.player.getGameProfile().getId().toString())) {
								Raion.INSTANCE.getEventManager()
									.dispatchEvent(new PlayerJoinEvent(playerData.getProfile()));
							}
						}
					}
					if (packet.getAction() == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
						for (SPacketPlayerListItem.AddPlayerData playerData : packet.getEntries()) {
							if (!playerData.getProfile()
								.getId()
								.toString()
								.equals(mc.player.getGameProfile().getId().toString())) {
								Raion.INSTANCE.getEventManager()
									.dispatchEvent(new PlayerLeaveEvent(playerData.getProfile()));
							}
						}
					}
				}
			}
		}
	}
	
	public void unload() {
		Raion.INSTANCE.getEventManager().removeEventListener(this);
	}
	
}
