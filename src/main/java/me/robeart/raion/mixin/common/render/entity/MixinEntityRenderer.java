package me.robeart.raion.mixin.common.render.entity;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.render.*;
import me.robeart.raion.client.imixin.IEntityRenderer;
import me.robeart.raion.client.module.player.FreecamModule;
import me.robeart.raion.client.module.render.ViewportModule;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IEntityRenderer {
	@Shadow
	protected abstract void setupCameraTransform(float partialTicks, int pass);
	
	public void setupCameraTransform0(float partialTicks, int pass) {
		this.setupCameraTransform(partialTicks, pass);
	}
	
	@Inject(method = "updateLightmap", at = @At(value = "RETURN", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;updateDynamicTexture()V"), require = 1)
	private void updateLightmapWrapper(float partialTicks, CallbackInfo ci) {
		Raion.INSTANCE.getEventManager().dispatchEvent(new LightmapUpdateEvent());
	}
	
	@Inject(method = "renderWorldPass", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand:Z"))
	private void renderWorldPass(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
		Render3DEvent event = new Render3DEvent(partialTicks, finishTimeNano);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
	}
	
	@Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
	public void hurtCameraEffect(float partialTicks, CallbackInfo ci) {
		HurtCameraEffectEvent event = new HurtCameraEffectEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Inject(method = "setupFog", at = @At(value = "HEAD"), cancellable = true)
	public void setupFog(int startCoords, float partialTicks, CallbackInfo ci) {
		SetupFogEvent event = new SetupFogEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Inject(method = "orientCamera", at = @At("HEAD"), cancellable = true)
	private void orientCamera(float partialTicks, CallbackInfo ci) {
		OrientCameraEvent event = new OrientCameraEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Redirect(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ActiveRenderInfo;getBlockStateAtEntityViewpoint(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/block/state/IBlockState;"))
	public IBlockState getBlockStateAtEntityViewpoint(World worldIn, Entity entityIn, float p_186703_2_) {
		BlockStateAtEntityViewpointEvent event = new BlockStateAtEntityViewpointEvent(ActiveRenderInfo.getBlockStateAtEntityViewpoint(worldIn, entityIn, p_186703_2_));
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		return event.getiBlockState();
	}
	
	@Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getRenderViewEntity()Lnet/minecraft/entity/Entity;"))
	private Entity redirectMouseOver(Minecraft mc) {
		if (FreecamModule.INSTANCE.getState()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
				return mc.player;
			}
		}
		return mc.getRenderViewEntity();
	}
	
	@Redirect(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;turn(FF)V"))
	private void redirectTurn(EntityPlayerSP entityPlayerSP, float yaw, float pitch) {
		try {
			Minecraft mc = Minecraft.getMinecraft();
			if (FreecamModule.INSTANCE.getState()) {
				if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
					mc.player.turn(yaw, pitch);
				}
				else {
					Objects.requireNonNull(mc.getRenderViewEntity(), "Render Entity").turn(yaw, pitch);
				}
				return;
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
			return;
		}
		entityPlayerSP.turn(yaw, pitch);
	}
	
	@Redirect(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isSpectator()Z"))
	public boolean redirectIsSpectator(EntityPlayerSP entityPlayerSP) {
		try {
			if (FreecamModule.INSTANCE.getState()) {
				return true;
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		try {
			if (entityPlayerSP != null) {
				return entityPlayerSP.isSpectator();
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		return false;
	}
	
	@Redirect(method = "setupCameraTransform", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
	private void projectInject0(float fovy, float aspect, float zNear, float zFar) {
		ViewportModule.INSTANCE.project(fovy, aspect, zNear, zFar);
	}
	
	@Redirect(method = "renderHand", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
	private void projectInject1(float fovy, float aspect, float zNear, float zFar) {
		ViewportModule.INSTANCE.project(fovy, aspect, zNear, zFar, true);
	}
	
	@Redirect(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
	private void projectInject2(float fovy, float aspect, float zNear, float zFar) {
		ViewportModule.INSTANCE.project(fovy, aspect, zNear, zFar);
	}
	
	@Redirect(method = "renderCloudsCheck", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
	private void projectInject3(float fovy, float aspect, float zNear, float zFar) {
		ViewportModule.INSTANCE.project(fovy, aspect, zNear, zFar);
	}
}
