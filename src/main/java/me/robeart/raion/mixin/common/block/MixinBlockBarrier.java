package me.robeart.raion.mixin.common.block;

import me.robeart.raion.client.module.render.VisionModule;
import net.minecraft.block.BlockBarrier;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author cookiedragon234 08/Jun/2020
 */
@Mixin(BlockBarrier.class)
public class MixinBlockBarrier {
	@Inject(method = "getRenderType", at = @At("RETURN"), cancellable = true)
	private void getRenderTypeWrapper(IBlockState state, CallbackInfoReturnable<EnumBlockRenderType> cir) {
		if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBarriers()) {
			cir.setReturnValue(EnumBlockRenderType.MODEL);
		}
	}
}
