package me.robeart.raion.mixin.common.gui;

import me.robeart.raion.client.module.player.FreecamModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author cookiedragon234 06/Jun/2020
 */
@Mixin(GuiIngame.class)
public class MixinGuiIngame {
	@Redirect(method = "renderGameOverlay", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/EntityPlayerSP;"))
	private EntityPlayerSP redirectOverlayPlayer(Minecraft mc) {
		Entity active = FreecamModule.INSTANCE.getActiveEntity();
		if (active instanceof EntityPlayerSP) {
			return (EntityPlayerSP) active;
		}
		return mc.player;
	}
	
	@Redirect(method = "renderPotionEffects", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/EntityPlayerSP;"))
	private EntityPlayerSP redirectPotionPlayer(Minecraft mc) {
		Entity active = FreecamModule.INSTANCE.getActiveEntity();
		if (active instanceof EntityPlayerSP) {
			return (EntityPlayerSP) active;
		}
		return mc.player;
	}
}
