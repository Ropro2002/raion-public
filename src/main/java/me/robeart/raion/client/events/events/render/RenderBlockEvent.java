package me.robeart.raion.client.events.events.render;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class RenderBlockEvent {
	
	private Block block;
	private BlockPos blockPos;
	
	public RenderBlockEvent(Block block, BlockPos blockPos) {
		this.block = block;
		this.blockPos = blockPos;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public void setBlock(Block block) {
		this.block = block;
	}
	
	public BlockPos getBlockPos() {
		return blockPos;
	}
	
	public void setBlockPos(BlockPos blockPos) {
		this.blockPos = blockPos;
	}
}
