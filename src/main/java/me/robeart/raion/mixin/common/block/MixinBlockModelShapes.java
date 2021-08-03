package me.robeart.raion.mixin.common.block;

import me.robeart.raion.client.module.render.VisionModule;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.init.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author cookiedragon234 08/Jun/2020
 */
@Mixin(BlockModelShapes.class)
public class MixinBlockModelShapes {
	@Inject(method = "getModelForState", at = @At("HEAD"), cancellable = true)
	private void getModelInject(IBlockState state, CallbackInfoReturnable<IBakedModel> cir) {
		if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBarriers()) {
			if (state.getBlock() == Blocks.BARRIER) {
				cir.setReturnValue(VisionModule.INSTANCE.getBarrierModel());
				return;
			}
		}
	}
	
}
