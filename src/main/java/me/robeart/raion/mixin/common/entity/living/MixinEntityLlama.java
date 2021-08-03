package me.robeart.raion.mixin.common.entity.living;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.entity.CanBeSteeredEvent;
import net.minecraft.entity.passive.EntityLlama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLlama.class)
public class MixinEntityLlama {
	
	@Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
	public void canBeSteered(CallbackInfoReturnable<Boolean> ci) {
		CanBeSteeredEvent event = new CanBeSteeredEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.setReturnValue(true);
	}
	
}
