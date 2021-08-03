package me.robeart.raion.mixin.common.render;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.render.RenderBlockEvent;
import me.robeart.raion.client.module.render.VisionModule;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRendererDispatcher.class)
public abstract class MixinBlockRendererDispatcher {
	
	@Inject(method = "renderBlock", at = @At("HEAD"))
	public void renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, BufferBuilder bufferBuilderIn, CallbackInfoReturnable<Boolean> ci) {
		Raion.INSTANCE.getEventManager().dispatchEvent(new RenderBlockEvent(state.getBlock(), pos));
	}
	
	@Redirect(method = "renderBlockBrightness", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockModelRenderer;renderModelBrightness(Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;FZ)V"))
	private void renderBrightnessRedir(BlockModelRenderer blockModelRenderer, IBakedModel model, IBlockState state, float brightness, boolean p_178266_4_) {
		if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBrightness()) {
			//brightness = 1f;
            GlStateManager.color(1f, 1f, 1f, 1f);
            return;
		}
		
		blockModelRenderer.renderModelBrightness(model, state, brightness, p_178266_4_);
	}
	
	@Redirect(method = "renderBlockBrightness", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ChestRenderer;renderChestBrightness(Lnet/minecraft/block/Block;F)V"))
	private void renderChestBrightnessRedir(ChestRenderer chestRenderer, Block blockIn, float color) {
		if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBrightness()) {
			//color = 1f;
            GlStateManager.color(1f, 1f, 1f, 1f);
            return;
		}
		
		chestRenderer.renderChestBrightness(blockIn, color);
	}
}
