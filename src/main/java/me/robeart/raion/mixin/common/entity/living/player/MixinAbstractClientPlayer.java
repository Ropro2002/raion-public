package me.robeart.raion.mixin.common.entity.living.player;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.player.LocateCapeEvent;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer {
	
	@Shadow
	@Nullable
	protected abstract NetworkPlayerInfo getPlayerInfo();
	
	@Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
	public void preGetLocationCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
		NetworkPlayerInfo info = this.getPlayerInfo();
		if (info != null) {
			LocateCapeEvent event = new LocateCapeEvent(info.getGameProfile().getId());
			Raion.INSTANCE.getEventManager().dispatchEvent(event);
			if (event.isCanceled()) callbackInfoReturnable.setReturnValue(event.getResourceLocation());
		}
	}
}
