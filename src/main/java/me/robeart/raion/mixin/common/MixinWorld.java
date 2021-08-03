package me.robeart.raion.mixin.common;

import me.robeart.raion.client.module.render.VisionModule;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author cookiedragon234 30/Mar/2020
 */
@Mixin(World.class)
public class MixinWorld {
	@Inject(method = "getSunBrightnessFactor", at = @At("RETURN"), cancellable = true, remap = false)
	private void getBrightnessOfSun(float partialTicks, CallbackInfoReturnable<Float> cir) {
		if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBrightness()) {
			cir.setReturnValue(1f);
		}
	}
	
	@Inject(method = "getSunBrightnessBody", at = @At("RETURN"), cancellable = true, remap = false)
	private void getBrightnessBodyOfSun(float partialTicks, CallbackInfoReturnable<Float> cir) {
		if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBrightness()) {
			cir.setReturnValue(1f);
		}
	}
	
	@Inject(method = "checkLightFor", at = @At("HEAD"), cancellable = true)
	private void checkLightForWrapper(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBrightness()) {
			cir.setReturnValue(false);
		}
	}

	/*@Inject(method = "removeEntity", at = @At("HEAD"), cancellable = true)
	private void removeEntity(Entity entity, CallbackInfo ci) {
		RemoveEntityEvent event = new RemoveEntityEvent(entity);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if(event.isCanceled()) ci.cancel();
	}*/
}
