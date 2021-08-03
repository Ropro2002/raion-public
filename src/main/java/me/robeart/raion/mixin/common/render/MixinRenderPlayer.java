package me.robeart.raion.mixin.common.render;

import me.robeart.raion.client.module.player.FreecamModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author cookiedragon234 30/Apr/2020
 */
@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {
	@Redirect(method = "doRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;isUser()Z"))
	private boolean isUserRedirect(AbstractClientPlayer abstractClientPlayer) {
		Minecraft mc = Minecraft.getMinecraft();
		if (FreecamModule.INSTANCE.getState()) {
			return abstractClientPlayer.isUser() && abstractClientPlayer == mc.getRenderViewEntity();
		}
		
		return abstractClientPlayer.isUser();
	}
}
