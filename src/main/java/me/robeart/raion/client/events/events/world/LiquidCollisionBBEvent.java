package me.robeart.raion.client.events.events.world;

import me.robeart.raion.client.events.EventCancellable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class LiquidCollisionBBEvent extends EventCancellable {
	
	private AxisAlignedBB boundingBox;
	private BlockPos blockPos;
	
	public LiquidCollisionBBEvent(AxisAlignedBB boundingBox, BlockPos blockPos) {
		this.boundingBox = boundingBox;
		this.blockPos = blockPos;
	}
	
	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}
	
	public void setBoundingBox(AxisAlignedBB boundingBox) {
		this.boundingBox = boundingBox;
	}
	
	public BlockPos getBlockPos() {
		return this.blockPos;
	}
	
	public void setBlockPos(BlockPos blockPos) {
		this.blockPos = blockPos;
	}
	
}
