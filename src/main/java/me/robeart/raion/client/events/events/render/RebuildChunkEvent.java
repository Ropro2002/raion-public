package me.robeart.raion.client.events.events.render;

import me.robeart.raion.client.events.EventStageable;
import net.minecraft.client.renderer.chunk.RenderChunk;

public class RebuildChunkEvent extends EventStageable {
	
	private RenderChunk renderChunk;
	
	public RebuildChunkEvent(EventStage stage, RenderChunk renderChunk) {
		super(stage);
		this.renderChunk = renderChunk;
	}
	
	public RenderChunk getRenderChunk() {
		return renderChunk;
	}
	
	public void setRenderChunk(RenderChunk renderChunk) {
		this.renderChunk = renderChunk;
	}
}
