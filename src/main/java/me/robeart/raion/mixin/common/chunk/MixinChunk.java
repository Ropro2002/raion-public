package me.robeart.raion.mixin.common.chunk;

import me.robeart.raion.client.module.render.VisionModule;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author cookiedragon234 08/Jun/2020
 */
@Mixin(Chunk.class)
public class MixinChunk {
	@Inject(method = "getLightFor", at = @At("HEAD"), cancellable = true)
	private void injectGetLightFor(EnumSkyBlock type, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		try {
			if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBrightness()) {
				//cir.setReturnValue(15);
				//return;
			}
		} catch (Throwable t) {}
	}
	
	@Inject(method = "getLightSubtracted", at = @At("HEAD"), cancellable = true)
	private void getLightSubtractedInject(BlockPos pos, int amount, CallbackInfoReturnable<Integer> cir) {
		try {
			if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBrightness()) {
				//cir.setReturnValue(15);
				//return;
			}
		} catch (Throwable t) {}
	}
}
