package me.robeart.raion.mixin.common;

import me.robeart.raion.client.module.render.VisionModule;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * @author cookiedragon234 08/Jun/2020
 */
@Mixin(WorldClient.class)
public class MixinWorldClient {
	@Inject(method = "showBarrierParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;spawnParticle(Lnet/minecraft/util/EnumParticleTypes;DDDDDD[I)V"), cancellable = true)
	private void renderBarrierParticleInject(int x, int y, int z, int offset, Random random, boolean holdingBarrier, BlockPos.MutableBlockPos pos, CallbackInfo ci) {
		if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBarriers()) {
			ci.cancel();
			return;
		}
	}
}
