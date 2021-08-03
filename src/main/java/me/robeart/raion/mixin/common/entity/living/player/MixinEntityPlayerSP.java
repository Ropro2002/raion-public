package me.robeart.raion.mixin.common.entity.living.player;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.player.*;
import me.robeart.raion.client.events.events.render.CloseScreenEvent;
import me.robeart.raion.client.imixin.IEntityPlayerSP;
import me.robeart.raion.client.module.player.FreecamModule;
import me.robeart.raion.mixin.common.entity.MixinEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends MixinEntity implements IEntityPlayerSP {
	
	@Override @Accessor(value = "horseJumpPower")
	public abstract void setHorseJumpPower(float horseJumpPower);
	
	@Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
	public void onUpdate(CallbackInfo ci) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.player == null || mc.world == null) return;
		OnUpdateEvent event = new OnUpdateEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
	}
	
	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	public void sendChatMessage(String message, CallbackInfo callback) {
		//TODO Make Event for this?
		if (message.startsWith(Raion.INSTANCE.getCommandManager().getPrefix())) {
			Raion.INSTANCE.getCommandManager().executeCommand(message.substring(1));
			callback.cancel();
		}
	}
	
	@Inject(method = "closeScreen", at = @At("HEAD"), cancellable = true)
	public void closeScreen(CallbackInfo ci) {
		CloseScreenEvent event = new CloseScreenEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
	public void swingArm(EnumHand hand, CallbackInfo ci) {
		SwingArmEvent event = new SwingArmEvent(hand);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Inject(method = "move", at = @At("HEAD"), cancellable = true)
	public void move(MoverType type, double x, double y, double z, CallbackInfo ci) {
		MoveEvent event = new MoveEvent(type, x, y, z);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
	public void onUpdateWalkingPlayerPre(CallbackInfo ci) {
		UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(EventStageable.EventStage.PRE);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Inject(method = "onUpdateWalkingPlayer", at = @At("TAIL"), cancellable = true)
	public void onUpdateWalkingPlayerPost(CallbackInfo ci) {
		UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(EventStageable.EventStage.POST);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
	public void pushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> ci) {
		PushOutOfBlocksEvent event = new PushOutOfBlocksEvent(x, y, z);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.setReturnValue(false);
	}
	
	@Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isCurrentViewEntity()Z"))
	private boolean redirectIsCurrentViewEntity(EntityPlayerSP entityPlayerSP) {
		Minecraft mc = Minecraft.getMinecraft();
		if (FreecamModule.INSTANCE.getState()) {
			return entityPlayerSP == mc.player;
		}
		
		return mc.getRenderViewEntity() == entityPlayerSP;
	}
	
	@Redirect(method = "updateEntityActionState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isCurrentViewEntity()Z"))
	private boolean redirectIsCurrentViewEntity2(EntityPlayerSP entityPlayerSP) {
		Minecraft mc = Minecraft.getMinecraft();
		if (FreecamModule.INSTANCE.getState()) {
			return entityPlayerSP == mc.player;
		}
		
		return mc.getRenderViewEntity() == entityPlayerSP;
	}
}
