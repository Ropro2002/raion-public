package me.robeart.raion.mixin.common.render;

import net.minecraft.client.renderer.chunk.RenderChunk;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderChunk.class)
public abstract class MixinRenderChunk {

    /*@Redirect(method = "rebuildChunk", at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraft/block/Block;canRenderInLayer(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/BlockRenderLayer;)Z"))
    public boolean canRenderInLayer(Block block, IBlockState iBlockState, BlockRenderLayer blockRenderLayer) {
        if (Raion.INSTANCE.getModuleManager().getModule(XrayModule.class).getState()) {
            if (!(blockRenderLayer == BlockRenderLayer.TRANSLUCENT))
                return XrayModule.shouldXray(block) && (iBlockState.getBlock().getRenderLayer() == blockRenderLayer);
            else return true;
        } else return (block.getRenderLayer() == blockRenderLayer);
    }*/
	
}
