package me.robeart.raion.mixin.common.network;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.world.ChunkEvent;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
	SPacketChunkData data = null;
	
	@Inject(method = "handleChunkData",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;read(Lnet/minecraft/network/PacketBuffer;IZ)V"),
		locals = LocalCapture.CAPTURE_FAILHARD)
	private void read(SPacketChunkData data, CallbackInfo info, Chunk chunk) {
		this.data = data;
	}
	
	@Redirect(method = "handleChunkData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;read(Lnet/minecraft/network/PacketBuffer;IZ)V"))
	private void readRedir(Chunk chunk, PacketBuffer buf, int availableSections, boolean groundUpContinuous) {
		chunk.read(buf, availableSections, groundUpContinuous);
		ChunkEvent event = new ChunkEvent(chunk, data);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
	}
	
}
