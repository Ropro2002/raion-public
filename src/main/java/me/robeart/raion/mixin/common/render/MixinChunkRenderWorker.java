package me.robeart.raion.mixin.common.render;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.render.FreeRenderBuilderEvent;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderWorker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkRenderWorker.class)
public abstract class MixinChunkRenderWorker {
	
	@Inject(method = "freeRenderBuilder", at = @At(value = "HEAD"))
	private void freeRenderBuilder(ChunkCompileTaskGenerator taskGenerator, CallbackInfo ci) {
		Raion.INSTANCE.getEventManager()
			.dispatchEvent(new FreeRenderBuilderEvent(taskGenerator, taskGenerator.getRenderChunk()));
	}
	
}

