package me.robeart.raion.client.events.events.render;

import me.robeart.raion.client.events.EventCancellable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;

public class CanRenderInLayerEvent extends EventCancellable {
	
	private Block block;
	private IBlockState iBlockState;
	private BlockRenderLayer blockRenderLayer;
	
	public CanRenderInLayerEvent(Block block, IBlockState iBlockState, BlockRenderLayer blockRenderLayer) {
		this.block = block;
		this.iBlockState = iBlockState;
		this.blockRenderLayer = blockRenderLayer;
	}
	
	public Block getBlock() {
		return this.block;
	}
	
	public void setBlock(Block block) {
		this.block = block;
	}
	
	public IBlockState getiBlockState() {
		return this.iBlockState;
	}
	
	public void setiBlockState(IBlockState iBlockState) {
		this.iBlockState = iBlockState;
	}
	
	public BlockRenderLayer getBlockRenderLayer() {
		return this.blockRenderLayer;
	}
	
	public void setBlockRenderLayer(BlockRenderLayer blockRenderLayer) {
		this.blockRenderLayer = blockRenderLayer;
	}
}
