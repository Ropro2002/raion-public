package me.robeart.raion.mixin.common.entity.living;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.entity.AbstractHorseSaddledEvent;
import me.robeart.raion.client.events.events.entity.CanBeSteeredEvent;
import net.minecraft.entity.passive.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractHorse.class, priority = 9998)
public abstract class MixinAbstractHorse {
	
	@Inject(method = "isHorseSaddled", at = @At("HEAD"), cancellable = true)
	private void isHorseSaddled(CallbackInfoReturnable<Boolean> ci) {
		AbstractHorseSaddledEvent event = new AbstractHorseSaddledEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.setReturnValue(event.isCanceled());
	}
	
	@Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
	public void canBeSteered(CallbackInfoReturnable<Boolean> ci) {
		CanBeSteeredEvent event = new CanBeSteeredEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.setReturnValue(event.isCanceled());
	}
}
