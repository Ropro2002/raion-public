package me.robeart.raion.client.module.misc;

import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.network.PacketReceiveEvent;
import me.robeart.raion.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumHand;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class AutoFishModule extends Module {
	
	public AutoFishModule() {
		super("AutoFish", "Automatically fishes using your fishing rod", Category.MISC);
	}
	
	@Listener
	private void onPacketReceive(PacketReceiveEvent event) {
		if ((event.getPacket() instanceof SPacketEntityVelocity) && event.getStage() == EventStageable.EventStage.PRE) {
			SPacketEntityVelocity packet = (SPacketEntityVelocity) event.getPacket();
			if ((mc.player.getHeldItemMainhand().getItem() instanceof ItemFishingRod)) {
				Entity entity = mc.world.getEntityByID(packet.getEntityID());
				if ((entity instanceof EntityFishHook) && (((EntityFishHook) entity).getAngler() == mc.player)) {
					if ((entity.motionX == 0.0D) && (entity.motionY <= -0.02D) && (entity.motionZ == 0.0D)) {
						mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
						mc.player.swingArm(EnumHand.MAIN_HAND);
						mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
						mc.player.swingArm(EnumHand.MAIN_HAND);
					}
				}
			}
		}
	}
}
