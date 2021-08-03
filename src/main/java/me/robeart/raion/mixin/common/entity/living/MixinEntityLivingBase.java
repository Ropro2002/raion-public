package me.robeart.raion.mixin.common.entity.living;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.entity.EntityLivingBaseTravelEvent;
import me.robeart.raion.client.events.events.entity.ShouldStopElytraEvent;
import me.robeart.raion.client.events.events.player.ItemPickupEvent;
import me.robeart.raion.client.module.movement.ElytraFlightModule;
import me.robeart.raion.client.module.movement.NoSlowDownModule;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {
	
	public MixinEntityLivingBase() {
		super(Minecraft.getMinecraft().world);
	}
	
	@Inject(method = "onItemPickup", at = @At("HEAD"), cancellable = true)
	public void onItemPickup(Entity entityIn, int quantity, CallbackInfo ci) {
		Raion.INSTANCE.getEventManager().dispatchEvent(new ItemPickupEvent(entityIn, quantity));
	}
	
	/**
	 * @author cats
	 * @since 20 Mar 2020
	 * Leaving this here so y'all know who to yell at if (when) it breaks
	 */
	@Inject(method = "travel", at = @At("HEAD"), cancellable = true)
	private void travel(float strafe, float vertical, float forward, CallbackInfo ci) {
		final EntityLivingBaseTravelEvent event = new EntityLivingBaseTravelEvent(this);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Redirect(method = "travel", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isRemote:Z", ordinal = 1))
	private boolean isWorldRemoteWrapper(World world) {
		// Smooth elytra
		ShouldStopElytraEvent event = new ShouldStopElytraEvent(world.isRemote);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		return event.isWorldRemote();
	}
	
	@Inject(method = "isElytraFlying", at = @At("RETURN"), cancellable = true)
	private void isElytraFlying(CallbackInfoReturnable<Boolean> cir) {
		ElytraFlightModule module = Raion.INSTANCE.getModuleManager().getModuleGeneric(ElytraFlightModule.class);
		boolean val = cir.getReturnValue();
		cir.setReturnValue(module.fakeElytraFly() && val);
	}
	
	/*@Redirect(method = "moveRelative", at = @At(value = "INVOKE", target = "isInWater()Z"))
	private boolean redirIsInWater(Entity entityLivingBase) {
		try {
			if (NoSlowDownModule.INSTANCE.getState() && NoSlowDownModule.INSTANCE.getLiquids()) {
				return false;
			}
		} catch (Throwable t) { t.printStackTrace(); }
		return entityLivingBase.isInWater();
	}
	
	@Redirect(method = "moveRelative", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;isInLava()Z"))
	private boolean redirIsInLava(EntityLivingBase entityLivingBase) {
		try {
			if (NoSlowDownModule.INSTANCE.getState() && NoSlowDownModule.INSTANCE.getLiquids()) {
				return false;
			}
		} catch (Throwable t) { t.printStackTrace(); }
		return entityLivingBase.isInLava();
	}*/
	
	@Redirect(method = "handleJumpWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/attributes/IAttributeInstance;getAttributeValue()D"))
	private double redirSpeedWater(IAttributeInstance iAttributeInstance) {
		try {
			if (NoSlowDownModule.INSTANCE.getState() && NoSlowDownModule.INSTANCE.getLiquidsUp()) {
				return 1.0;
			}
		} catch (Throwable t) { t.printStackTrace(); }
		return iAttributeInstance.getAttributeValue();
	}
	
	@Redirect(method = "handleJumpLava", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/attributes/IAttributeInstance;getAttributeValue()D"))
	private double redirSpeedLava(IAttributeInstance iAttributeInstance) {
		try {
			if (NoSlowDownModule.INSTANCE.getState() && NoSlowDownModule.INSTANCE.getLiquidsUp()) {
				return 1.0;
			}
		} catch (Throwable t) { t.printStackTrace(); }
		return iAttributeInstance.getAttributeValue();
	}
	
	@Inject(method = "getWaterSlowDown", at = @At("HEAD"), cancellable = true)
	private void redirWaterSlowdown(CallbackInfoReturnable<Float> cir) {
		try {
			if (NoSlowDownModule.INSTANCE.getState() && NoSlowDownModule.INSTANCE.getLiquids()) {
				cir.setReturnValue(1f);
			}
		} catch (Throwable t) { t.printStackTrace(); }
	}
	
	@ModifyConstant(method = "onLivingUpdate", constant = @Constant(doubleValue = 0.800000011920929D))
	private double redirWaterSlowdown2() {
		try {
			if (NoSlowDownModule.INSTANCE.getState() && NoSlowDownModule.INSTANCE.getLiquidsUp()) {
				return 1.0;
			}
		} catch (Throwable t) { t.printStackTrace(); }
		return 0.800000011920929D;
	}
}
