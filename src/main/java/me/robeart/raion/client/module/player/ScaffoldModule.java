package me.robeart.raion.client.module.player;

import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.player.UpdateWalkingPlayerEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.value.DoubleValue;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.HashMap;
import java.util.Map;

public class ScaffoldModule extends Module {
	
	public DoubleValue range = new DoubleValue("Range", 4.2, 1, 6, 0.1);
	private HashMap<BlockPos, Integer> lastPlaced = new HashMap<>();
	
	public ScaffoldModule() {
		super("Scaffold", "Places blocks under you", Category.PLAYER);
	}
	
	@Listener
	private void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
		if (event.getStage() != EventStageable.EventStage.PRE || mc.player.noClip) return;
		
	}
	
	public void onUpdate() {
		HashMap<BlockPos, Integer> tempMap = new HashMap<>();
		for (Map.Entry<BlockPos, Integer> e : lastPlaced.entrySet()) {
			if (e.getValue() > 0) tempMap.put(e.getKey(), e.getValue() - 1);
		}
		lastPlaced.clear();
		lastPlaced.putAll(tempMap);
		
		if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock)) return;
		
		double range = this.range.getValue();
		for (int r = 0; r < 5; r++) {
			Vec3d r1 = new Vec3d(0, -0.85, 0);
			if (r == 1) r1 = r1.add(range, 0, 0);
			if (r == 2) r1 = r1.add(-range, 0, 0);
			if (r == 3) r1 = r1.add(0, 0, range);
			if (r == 4) r1 = r1.add(0, 0, -range);
			Block block = ((ItemBlock) mc.player.getHeldItemMainhand().getItem()).getBlock();
			if (block.canPlaceBlockAt(mc.world, new BlockPos(mc.player.getPositionVector().add(r1)))) {
				placeBlockAuto(new BlockPos(mc.player.getPositionVector().add(r1)));
			}
		}
	}
	
	public void placeBlockAuto(BlockPos block) {
        /*if (lastPlaced.containsKey(block)) return;
        for (EnumFacing d : EnumFacing.values()) {
            if (WorldUtils.RIGHTCLICKABLE_BLOCKS.contains(mc.world.getBlockState(block.offset(d)).getBlock()) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }
            mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND,
                    new BlockRayTraceResult(new Vec3d(block), d.getOpposite(), block.offset(d), false)));
            mc.player.swingArm(Hand.MAIN_HAND);
            mc.world.playSound(block, SoundEvents.BLOCK_NOTE_BLOCK_HAT, SoundCategory.BLOCKS, 1f, 1f, false);
            if (WorldUtils.RIGHTCLICKABLE_BLOCKS.contains(mc.world.getBlockState(block.offset(d)).getBlock())) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, Action.STOP_SNEAKING));
            }
            lastPlaced.put(block, 5);
            return;
        }*/
	}
}
