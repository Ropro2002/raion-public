package me.robeart.raion.mixin.common.gui;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.render.RenderBossBarEvent;
import net.minecraft.client.gui.GuiBossOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author cats
 */
@Mixin(GuiBossOverlay.class)
public class MixinGuiBossOverlay {
	
	@Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
	private void renderBossHealth(CallbackInfo ci) {
		final RenderBossBarEvent event = new RenderBossBarEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
}
