package me.robeart.raion.client.module.player;

import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.network.PacketSendEvent;
import me.robeart.raion.client.imixin.ICPacketCloseWindow;
import me.robeart.raion.client.module.Module;
import net.minecraft.network.play.client.CPacketCloseWindow;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author Robeart
 */
public class XCarryModule extends Module {
	
	public XCarryModule() {
		super("XCarry", "Allows you to carry items in your crafting table slots", Category.PLAYER);
	}
	
	@Override
	public void onDisable() {
		if (mc.world != null) {
			mc.getConnection().sendPacket(new CPacketCloseWindow(mc.player.inventoryContainer.windowId));
		}
	}
	
	@Listener
	private void onSendPacket(PacketSendEvent event) {
		if (event.getStage() != EventStageable.EventStage.PRE) return;
		if (event.getPacket() instanceof CPacketCloseWindow) {
			CPacketCloseWindow packet = (CPacketCloseWindow) event.getPacket();
			if (((ICPacketCloseWindow) packet).getWindowId() == mc.player.inventoryContainer.windowId)
				event.setCanceled(true);
		}
	}
	
}
