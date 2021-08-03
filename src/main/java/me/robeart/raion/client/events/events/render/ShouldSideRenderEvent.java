package me.robeart.raion.client.events.events.render;

import me.robeart.raion.client.events.EventCancellable;
import net.minecraft.block.Block;

public class ShouldSideRenderEvent extends EventCancellable {
	
	private Block block;
	
	public ShouldSideRenderEvent(Block block) {
		this.block = block;
	}
	
	public Block getBlock() {
		return this.block;
	}
	
	public void setBlock(Block block) {
		this.block = block;
	}
	
	
}
