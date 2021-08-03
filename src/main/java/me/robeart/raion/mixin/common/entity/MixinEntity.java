package me.robeart.raion.mixin.common.entity;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.entity.AddEntityVelocityEvent;
import me.robeart.raion.client.events.events.entity.ShouldWalkOffEdgeEvent;
import me.robeart.raion.client.imixin.IEntity;
import me.robeart.raion.client.module.movement.VelocityModule;
import me.robeart.raion.client.module.render.VisionModule;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity {
	@Shadow
	protected boolean inPortal;
	
	@Shadow
	protected abstract void setFlag(int flag, boolean set);
	
	public void setFlag0(int flag, boolean set) {
		this.setFlag(flag, set);
	}
	
	@Accessor(value = "isInWeb")
	public abstract void setIsInWeb(boolean isInWeb);
	
	@Accessor(value = "inPortal")
	public abstract void setInPortal(boolean inPortal);
	
	@ModifyVariable(method = "addVelocity", at = @At(value = "HEAD"), ordinal = 0)
	private double modifyVariable1(double x) {
		AddEntityVelocityEvent event = new AddEntityVelocityEvent((Entity) (Object) this, x, x, x);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		return event.x;
	}
	
	@ModifyVariable(method = "addVelocity", at = @At(value = "HEAD"), ordinal = 1)
	private double modifyVariable2(double y) {
		AddEntityVelocityEvent event = new AddEntityVelocityEvent((Entity) (Object) this, y, y, y);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		return event.y;
	}
	
	@ModifyVariable(method = "addVelocity", at = @At(value = "HEAD"), ordinal = 2)
	private double modifyVariable3(double z) {
		AddEntityVelocityEvent event = new AddEntityVelocityEvent((Entity) (Object) this, z, z, z);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		return event.z;
	}
	
	@Redirect(
		method = "move",
		slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;onGround:Z", ordinal = 0)),
		at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z", ordinal = 0)
	)
	private boolean isSneaking(Entity entity) {
		ShouldWalkOffEdgeEvent event = new ShouldWalkOffEdgeEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		return (event.isCanceled() || entity.isSneaking());
	}
	
	@Inject(method = "getBrightness", at = @At("HEAD"), cancellable = true)
	private void injectGetBrightness(CallbackInfoReturnable<Float> cir) {
		if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBrightness()) {
			cir.setReturnValue(1f);
			return;
		}
	}
	
	@Inject(method = "isPushedByWater", at = @At("HEAD"), cancellable = true)
	private void isPushedInject(CallbackInfoReturnable<Boolean> cir) {
		if (VelocityModule.INSTANCE.getState()) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}
}
