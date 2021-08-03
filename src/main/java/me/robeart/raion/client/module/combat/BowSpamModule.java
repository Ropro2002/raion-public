package me.robeart.raion.client.module.combat;

import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.Timer;
import me.robeart.raion.client.value.DoubleValue;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class BowSpamModule extends Module {
	
	public DoubleValue delay = new DoubleValue("Delay", 0, 0, 15, 0.1);
	private Timer delaytimer = new Timer();
	
	public BowSpamModule() {
		super("BowSpam", "Releases the bow automatically", Category.COMBAT);
	}
	
	@Listener
	public void onUpdate(OnUpdateEvent event) {
		if (!delaytimer.passed(delay.getValue() * 100)) return;
		if (((mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow)) &&
			(mc.player.isHandActive()) && (mc.player.getItemInUseMaxCount() >= 3)) {
			mc.player.connection.sendPacket(new CPacketPlayerDigging(net.minecraft.network.play.client.CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player
				.getHorizontalFacing()));
			mc.player.connection.sendPacket(new net.minecraft.network.play.client.CPacketPlayerTryUseItem(mc.player.getActiveHand()));
			mc.player.stopActiveHand();
			//System.out.println(delaytimer.getTime());
			delaytimer.reset();
		}
		
	}
	
}
