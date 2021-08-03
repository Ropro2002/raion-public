package me.robeart.raion.client.module.movement;

import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.network.PacketSendEvent;
import me.robeart.raion.client.imixin.ICPacketPlayer;
import me.robeart.raion.client.module.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author cats
 */

public class NoHungerModule extends Module {
	
	public NoHungerModule() {
		super("NoHunger", "Decreases the speed of losing hunger", Category.MOVEMENT);
	}
	
	@Listener
	public void packetSend(PacketSendEvent event) {
		if (event.getStage() != EventStageable.EventStage.PRE) return;
		if (event.getPacket() instanceof CPacketPlayer) {
			final ICPacketPlayer packet = (ICPacketPlayer) event.getPacket();
			packet.setOnGround(false);
		}
	}
}
