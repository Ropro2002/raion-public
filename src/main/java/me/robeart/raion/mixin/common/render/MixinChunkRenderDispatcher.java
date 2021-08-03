package me.robeart.raion.mixin.common.render;

import com.google.common.util.concurrent.ListenableFuture;
import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.render.UploadChunkEvent;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRenderDispatcher.class)
public abstract class MixinChunkRenderDispatcher {
	
	@Inject(method = "uploadChunk", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/OpenGlHelper.useVbo()Z"))
	private void onUploadChunk(BlockRenderLayer a, BufferBuilder a2, RenderChunk a3, CompiledChunk a4, double a5, CallbackInfoReturnable<ListenableFuture<Object>> a6) {
		Raion.INSTANCE.getEventManager().dispatchEvent(new UploadChunkEvent(a3, a2));
	}
	
}
