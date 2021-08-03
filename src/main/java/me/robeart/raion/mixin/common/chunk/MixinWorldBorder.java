package me.robeart.raion.mixin.common.chunk;

import me.robeart.raion.client.module.player.InteractionTweaksModule;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author cookiedragon234 08/Jun/2020
 */
@Mixin(WorldBorder.class)
public class MixinWorldBorder {
	@Inject(method = "contains(Lnet/minecraft/util/math/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
	private void redirContains(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (InteractionTweaksModule.INSTANCE.getState() && InteractionTweaksModule.INSTANCE.getBypassWorldBorder()) {
			cir.setReturnValue(true);
			return;
		}
	}
	
	@Inject(method = "contains(Lnet/minecraft/util/math/ChunkPos;)Z", at = @At("HEAD"), cancellable = true)
	private void redirContains2(ChunkPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (InteractionTweaksModule.INSTANCE.getState() && InteractionTweaksModule.INSTANCE.getBypassWorldBorder()) {
			cir.setReturnValue(true);
			return;
		}
	}
	
	@Inject(method = "contains(Lnet/minecraft/util/math/AxisAlignedBB;)Z", at = @At("HEAD"), cancellable = true)
	private void redirContains2(AxisAlignedBB pos, CallbackInfoReturnable<Boolean> cir) {
		if (InteractionTweaksModule.INSTANCE.getState() && InteractionTweaksModule.INSTANCE.getBypassWorldBorder()) {
			cir.setReturnValue(true);
			return;
		}
	}
}
