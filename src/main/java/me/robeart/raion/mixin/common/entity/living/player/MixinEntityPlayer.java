package me.robeart.raion.mixin.common.entity.living.player;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.player.ApplyCollisionEvent;
import me.robeart.raion.client.events.events.player.IsEntityInsideOpaqueBlockEvent;
import me.robeart.raion.client.events.events.player.PushedByWaterEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public class MixinEntityPlayer {
	
	@Inject(method = "isEntityInsideOpaqueBlock", at = @At("HEAD"), cancellable = true)
	public void isEntityInsideOpaqueBlock(CallbackInfoReturnable<Boolean> ci) {
		IsEntityInsideOpaqueBlockEvent event = new IsEntityInsideOpaqueBlockEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.setReturnValue(false);
	}
	
	@Inject(method = "isPushedByWater", at = @At("RETURN"), cancellable = true)
	public void isPushedByWater(CallbackInfoReturnable<Boolean> ci) {
		PushedByWaterEvent event = new PushedByWaterEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.setReturnValue(false);
	}
	
	@Inject(method = "applyEntityCollision", at = @At("HEAD"), cancellable = true)
	public void applyEntityCollision(Entity entity, CallbackInfo ci) {
		ApplyCollisionEvent event = new ApplyCollisionEvent(entity);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
}
