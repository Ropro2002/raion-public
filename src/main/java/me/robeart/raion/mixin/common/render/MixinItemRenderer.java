package me.robeart.raion.mixin.common.render;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.render.RenderOverlayEvent;
import me.robeart.raion.client.imixin.IMixinItemRenderer;
import me.robeart.raion.client.module.player.FreecamModule;
import me.robeart.raion.client.module.render.NoRenderModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Robeart
 */
@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer implements IMixinItemRenderer {
	
	@Redirect(method = "setLightmap", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/EntityPlayerSP;"))
	private EntityPlayerSP redirectLightmapPlayer(Minecraft mc) {
		Entity active = FreecamModule.INSTANCE.getActiveEntity();
		if (active instanceof EntityPlayerSP) {
			return (EntityPlayerSP) active;
		}
		return mc.player;
	}
	
	@Redirect(method = "rotateArm", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/EntityPlayerSP;"))
	private EntityPlayerSP rotateArmPlayer(Minecraft mc) {
		Entity active = FreecamModule.INSTANCE.getActiveEntity();
		if (active instanceof EntityPlayerSP) {
			return (EntityPlayerSP) active;
		}
		return mc.player;
	}
	
	@Redirect(method = "renderItemInFirstPerson(F)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/EntityPlayerSP;"))
	private EntityPlayerSP redirectPlayer(Minecraft mc) {
		Entity active = FreecamModule.INSTANCE.getActiveEntity();
		if (active instanceof EntityPlayerSP) {
			return (EntityPlayerSP) active;
		}
		return mc.player;
	}
	
	@Inject(method = "renderOverlays", at = @At("HEAD"), cancellable = true)
	private void renderOverlaysInject(float partialTicks, CallbackInfo ci) {
		if (FreecamModule.INSTANCE.getState() || NoRenderModule.INSTANCE.getState()) {
			ci.cancel();
			return;
		}
	}
	
	@Redirect(method = "renderOverlays", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/EntityPlayerSP;"))
	private EntityPlayerSP renderOverlaysPlayer(Minecraft mc) {
		Entity active = FreecamModule.INSTANCE.getActiveEntity();
		if (active instanceof EntityPlayerSP) {
			return (EntityPlayerSP) active;
		}
		return mc.player;
	}
	
	@Inject(method = "renderWaterOverlayTexture", at = @At("HEAD"), cancellable = true)
	private void renderWaterOverlayTexture(float partialTicks, CallbackInfo ci) {
		RenderOverlayEvent event = new RenderOverlayEvent(RenderOverlayEvent.OverlayType.LIQUID);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
	private void renderFireInFirstPerson(CallbackInfo ci) {
		RenderOverlayEvent event = new RenderOverlayEvent(RenderOverlayEvent.OverlayType.FIRE);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Inject(method = "renderItemInFirstPerson(F)V", at = @At("HEAD"), cancellable = true)
	private void renderItemInFirstPerson(float partialTicks, CallbackInfo ci) {
		RenderOverlayEvent event = new RenderOverlayEvent(RenderOverlayEvent.OverlayType.ITEM);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Accessor(value = "equippedProgressOffHand")
	public abstract void setEquippedProgressOffHand(float equippedProgressOffHand);
	
	@Accessor(value = "equippedProgressMainHand")
	public abstract void setEquippedProgressMainHand(float equippedProgressMainHand);
	
	@Redirect(method = "renderItemInFirstPerson(F)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;prevRotationPitch:F"))
	private float redirectPrevPitch(AbstractClientPlayer player) {
		Minecraft mc = Minecraft.getMinecraft();
		Entity entity = mc.getRenderViewEntity();
		if (entity != null) {
			return entity.prevRotationPitch;
		}
		return mc.player.prevRotationPitch;
	}
	
	@Redirect(method = "renderItemInFirstPerson(F)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;rotationPitch:F"))
	private float redirectPitch(AbstractClientPlayer player) {
		Minecraft mc = Minecraft.getMinecraft();
		Entity entity = mc.getRenderViewEntity();
		if (entity != null) {
			return entity.rotationPitch;
		}
		return mc.player.rotationPitch;
	}
	
	@Redirect(method = "renderItemInFirstPerson(F)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;prevRotationYaw:F"))
	private float redirectPrevYaw(AbstractClientPlayer player) {
		Minecraft mc = Minecraft.getMinecraft();
		Entity entity = mc.getRenderViewEntity();
		if (entity != null) {
			return entity.prevRotationYaw;
		}
		return mc.player.prevRotationYaw;
	}
	
	@Redirect(method = "renderItemInFirstPerson(F)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;rotationYaw:F"))
	private float redirectYaw(AbstractClientPlayer player) {
		Minecraft mc = Minecraft.getMinecraft();
		Entity entity = mc.getRenderViewEntity();
		if (entity != null) {
			return entity.rotationYaw;
		}
		return mc.player.rotationYaw;
	}
}
