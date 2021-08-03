package me.robeart.raion.client.module.player;

import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.network.PacketReceiveEvent;
import me.robeart.raion.client.imixin.ISPacketPlayerPosLook;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.value.BooleanValue;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author cats
 */

public class NoRotateModule extends Module {
	
	public BooleanValue force = new BooleanValue("Force Rotation", true);
	
	public NoRotateModule() {
		super("NoRotate", "Ignores the servers rotation packets", Category.PLAYER);
	}
	
	@Listener
	public void onReceive(PacketReceiveEvent event) {
		if (event.getStage() != EventStageable.EventStage.PRE) return;
		if (mc.player == null || mc.world == null) return;
		
		if (event.getPacket() instanceof SPacketPlayerPosLook) {
			final ISPacketPlayerPosLook rotationPacket = (ISPacketPlayerPosLook) event.getPacket();
			
			if (rotationPacket.getYaw() == mc.player.rotationYaw
				&& rotationPacket.getPitch() == mc.player.rotationPitch) return;
			
			if (this.force.getValue() &&
				mc.getConnection() != null) {
				mc.getConnection()
					.sendPacket(new CPacketPlayer.Rotation(rotationPacket.getYaw(), rotationPacket.getPitch(), mc.player.onGround));
			}
			
			rotationPacket.setYaw(mc.player.rotationYaw);
			
			rotationPacket.setPitch(mc.player.rotationPitch);
			
			if (this.force.getValue() &&
				mc.getConnection() != null) {
				mc.getConnection()
					.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
			}
		}
	}
}
