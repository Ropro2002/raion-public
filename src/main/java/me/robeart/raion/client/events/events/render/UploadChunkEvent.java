package me.robeart.raion.client.events.events.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.chunk.RenderChunk;

public class UploadChunkEvent {
	
	private RenderChunk renderChunk;
	private BufferBuilder bufferBuilder;
	
	public UploadChunkEvent(RenderChunk renderChunk, BufferBuilder bufferBuilder) {
		this.renderChunk = renderChunk;
		this.bufferBuilder = bufferBuilder;
	}
	
	public BufferBuilder getBufferBuilder() {
		return bufferBuilder;
	}
	
	public void setBufferBuilder(BufferBuilder bufferBuilder) {
		this.bufferBuilder = bufferBuilder;
	}
	
	public RenderChunk getRenderChunk() {
		return renderChunk;
	}
	
	public void setRenderChunk(RenderChunk renderChunk) {
		this.renderChunk = renderChunk;
	}
}
