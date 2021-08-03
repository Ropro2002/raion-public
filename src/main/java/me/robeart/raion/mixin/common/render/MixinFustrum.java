package me.robeart.raion.mixin.common.render;

import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author cookiedragon234 30/Apr/2020
 */
@Mixin(Frustum.class)
public class MixinFustrum {
	
	// This is slower than it ought to be, so I'll comment it out for now
	// TODO find a more elegant solution, todo for later as I haven't found one just yet
/*	@Inject(method = "isBoundingBoxInFrustum(Lnet/minecraft/util/math/AxisAlignedBB;)Z", at = @At("HEAD"), cancellable = true)
	public void injectIsBoundingBoxInFrustum(AxisAlignedBB bb, CallbackInfoReturnable<Boolean> ci) {
		if (FreecamModule.INSTANCE.getState()) {
			ci.setReturnValue(true);
        }
	}*/
}
