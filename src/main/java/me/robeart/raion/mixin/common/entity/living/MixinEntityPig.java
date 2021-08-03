package me.robeart.raion.mixin.common.entity.living;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.entity.CanBeSteeredEvent;
import me.robeart.raion.client.events.events.entity.PigTravelEvent;
import net.minecraft.entity.passive.EntityPig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityPig.class)
public class MixinEntityPig {
	
	@Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
	private void canBeSteered(CallbackInfoReturnable<Boolean> ci) {
		CanBeSteeredEvent event = new CanBeSteeredEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.setReturnValue(event.isCanceled());
	}
	
	@Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/EntityPig;setAIMoveSpeed(F)V"), cancellable = true)
	public void travel(float strafe, float vertical, float forward, CallbackInfo ci) {
		PigTravelEvent event = new PigTravelEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
}
