package me.robeart.raion.mixin.common.render.entity;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.render.RenderModelEvent;
import me.robeart.raion.client.events.events.render.RenderNameEvent;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> {
	
	@Shadow
	protected ModelBase mainModel;
	
	@Inject(method = "renderName", at = @At("HEAD"), cancellable = true)
	public void renderName(T entity, double x, double y, double z, CallbackInfo ci) {
		RenderNameEvent event = new RenderNameEvent(entity, x, y, z);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Inject(method = "renderModel", at = @At("RETURN"), cancellable = true)
	public void renderModel(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo ci) {
		RenderModelEvent event = new RenderModelEvent(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, this.mainModel);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
}
