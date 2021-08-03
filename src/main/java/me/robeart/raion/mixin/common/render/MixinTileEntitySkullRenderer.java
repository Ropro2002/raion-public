package me.robeart.raion.mixin.common.render;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.render.RenderChestEvent;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.tileentity.TileEntityChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Robeart
 */
@Mixin(TileEntityChestRenderer.class)
public abstract class MixinTileEntitySkullRenderer {
	
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void render(TileEntityChest te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo ci) {
		RenderChestEvent event = new RenderChestEvent(te, x, y, z);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
}
