package me.robeart.raion.client.module.combat;

import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.module.render.HoleESPModule;
import me.robeart.raion.client.util.Timer;
import me.robeart.raion.client.util.minecraft.MinecraftUtils;
import me.robeart.raion.client.value.DoubleValue;
import me.robeart.raion.client.value.IntValue;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author Robeart
 */
public class HoleFillerModule extends Module {
	
	public IntValue placeDelay = new IntValue("Place Delay", 50, 0, 1000, 1);
	public IntValue bpt = new IntValue("BPT", 3, 1, 8, 1);
	public DoubleValue range = new DoubleValue("Place Range", 4, 1, 10, 0.1);
	
	private int slot = 0;
	private Timer placeTimer = new Timer();
	
	public HoleFillerModule() {
		super("HoleFiller", "Places blocks inside of holes, preventing your enemy from sitting in them", Category.COMBAT);
	}
	
	@Override
	public void onEnable() {
		if (mc.player != null) slot = mc.player.inventory.currentItem;
	}
	
	@Override
	public void onDisable() {
		if (mc.player != null) mc.player.inventory.currentItem = slot;
	}
	
	@Listener
	public void onUpdate(OnUpdateEvent event) {
		if (!MinecraftUtils.isHoldingBlock(Blocks.OBSIDIAN)) slot = mc.player.inventory.currentItem;
		if (this.placeTimer.passed(this.placeDelay.getValue())) {
			int tickdelay = 0;
			for (BlockPos.MutableBlockPos pos : MinecraftUtils.getBlocksInRadiusMutable(range.getValue(), range.getValue())) {
				if (HoleESPModule.isHoleMutable(pos) != 0) {
					if (tickdelay >= bpt.getValue()) return;
					if (MinecraftUtils.canPlace(pos, Blocks.OBSIDIAN, true)) {
						if (!MinecraftUtils.holdBlock(Blocks.OBSIDIAN)) return;
						MinecraftUtils.place(pos);
						mc.player.inventory.currentItem = slot;
						tickdelay++;
					}
				}
			}
			placeTimer.reset();
		}
	}
	
}
