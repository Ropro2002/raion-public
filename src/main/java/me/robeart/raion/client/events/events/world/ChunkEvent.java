package me.robeart.raion.client.events.events.world;

import me.robeart.raion.client.events.EventCancellable;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.chunk.Chunk;

public class ChunkEvent extends EventCancellable {
	
	private Chunk chunk;
	private SPacketChunkData data;
	
	public ChunkEvent(Chunk chunk, SPacketChunkData data) {
		this.chunk = chunk;
		this.data = data;
	}
	
	public Chunk getChunk() {
		return chunk;
	}
	
	public SPacketChunkData getData() {
		return data;
	}
}
