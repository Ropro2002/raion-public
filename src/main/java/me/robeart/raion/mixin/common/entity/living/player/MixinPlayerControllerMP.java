package me.robeart.raion.mixin.common.entity.living.player;

import me.robeart.raion.client.imixin.IMixinPlayerControllerMP;
import me.robeart.raion.client.module.player.InteractionTweaksModule;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP implements IMixinPlayerControllerMP {
	@Inject(method = "resetBlockRemoving", at = @At("HEAD"), cancellable = true)
	private void resetBlockRemovingInject(CallbackInfo ci) {
		if (InteractionTweaksModule.INSTANCE.getState() && InteractionTweaksModule.INSTANCE.getStickyBreak()) {
			ci.cancel();
			return;
		}
	}
	
	@Invoker("syncCurrentPlayItem")
	@Override
	public abstract void invokeSyncCurrentPlayItem();
}
