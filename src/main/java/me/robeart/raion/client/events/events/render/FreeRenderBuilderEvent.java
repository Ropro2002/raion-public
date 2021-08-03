package me.robeart.raion.client.events.events.render;

import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.RenderChunk;

public class FreeRenderBuilderEvent {
	
	private ChunkCompileTaskGenerator chunkCompileTaskGenerator;
	private RenderChunk renderChunk;
	
	public FreeRenderBuilderEvent(ChunkCompileTaskGenerator chunkCompileTaskGenerator, RenderChunk renderChunk) {
		this.chunkCompileTaskGenerator = chunkCompileTaskGenerator;
		this.renderChunk = renderChunk;
	}
	
	public RenderChunk getRenderChunk() {
		return renderChunk;
	}
	
	public void setRenderChunk(RenderChunk renderChunk) {
		this.renderChunk = renderChunk;
	}
	
	public ChunkCompileTaskGenerator getChunkCompileTaskGenerator() {
		return chunkCompileTaskGenerator;
	}
	
	public void setChunkCompileTaskGenerator(ChunkCompileTaskGenerator chunkCompileTaskGenerator) {
		this.chunkCompileTaskGenerator = chunkCompileTaskGenerator;
	}
}
