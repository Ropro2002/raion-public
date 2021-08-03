package me.robeart.raion.mixin.common.block;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.render.ShouldSideRenderEvent;
import me.robeart.raion.client.module.render.VisionModule;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Block.class)
public abstract class MixinBlock {
	
	@Shadow
	@Final
	public Material material;
	
	@Inject(method = "shouldSideBeRendered", at = @At("HEAD"), cancellable = true)
	public void shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> ci) {
		ShouldSideRenderEvent event = new ShouldSideRenderEvent(blockState.getBlock());
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.setReturnValue(event.isCanceled());
	}
	
	@Inject(method = "getAmbientOcclusionLightValue", at = @At("RETURN"), cancellable = true)
	private void getAmbientOcclusionLightValue(final CallbackInfoReturnable<Float> ci) {
		if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBrightness()) {
			//ci.setReturnValue(1f);
			//return;
		}
	}
	
	@Inject(method = "getPackedLightmapCoords", at = @At("HEAD"), cancellable = true)
	private void getPackedLightmapCoordsWrapper(IBlockState state, IBlockAccess source, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBrightness()) {
			//cir.setReturnValue(15);
			//return;
		}
	}
	
	@Inject(method = "getLightValue(Lnet/minecraft/block/state/IBlockState;)I", at = @At("HEAD"), cancellable = true)
	private void getLightValueInject2(IBlockState state, CallbackInfoReturnable<Integer> cir) {
		if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBrightness()) {
			//cir.setReturnValue(15);
			//return;
		}
	}
	
	@Inject(method = "getLightValue(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)I", at = @At("HEAD"), cancellable = true, remap = false)
	private void getLightValueInject(IBlockState state, IBlockAccess world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBrightness()) {
			//cir.setReturnValue(15);
			//return;
		}
	}
	
	@Inject(method = "getRenderLayer", at = @At("HEAD"), cancellable = true)
	private void getRenderLayerInject(CallbackInfoReturnable<BlockRenderLayer> cir) {
		if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBarriers()) {
			if ((Object)this == Blocks.BARRIER) {
				cir.setReturnValue(BlockRenderLayer.CUTOUT);
				return;
			}
		}
	}

    /*@Inject(method = "getAmbientOcclusionLightValue", at = @At(value = "HEAD"), cancellable = true)
    private void getAmbientOcclusionLightValue(CallbackInfoReturnable<Float> a) {
        if (Raion.INSTANCE.getModuleManager().getModule(XrayModule.class).getState()) {
            a.setReturnValue(1.0f);
        }
    }*/
	
}
