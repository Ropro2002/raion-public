package me.robeart.raion.mixin.common.render;

import me.robeart.raion.client.module.render.VisionModule;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockModelRenderer.class)
public abstract class MixinBlockModelRenderer {
	
	@Shadow
	public abstract boolean renderModelSmooth(IBlockAccess worldIn, IBakedModel modelIn, IBlockState stateIn, BlockPos posIn, BufferBuilder buffer, boolean checkSides, long rand);
	
	@Redirect(method = "renderQuadsFlat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BufferBuilder;putBrightness4(IIII)V"))
	private void redirBrightness(BufferBuilder bufferBuilder, int vertex0, int vertex1, int vertex2, int vertex3) {
		if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBrightness()) {
			bufferBuilder.putBrightness4(15728640, 15728640, 15728640, 15728640);
		}
		else {
			bufferBuilder.putBrightness4(vertex0, vertex1, vertex2, vertex3);
		}
	}
	
	@Redirect(method = "renderQuadsFlat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BufferBuilder;putColorMultiplier(FFFI)V"))
	private void redirBrightness(BufferBuilder bufferBuilder, float red, float green, float blue, int vertexIndex) {
		//if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBrightness()) {
		//	bufferBuilder.putColorMultiplier(1f, 1f, 1f, vertexIndex);
		//}
		//else {
			bufferBuilder.putColorMultiplier(red, green, blue, vertexIndex);
		//}
	}
	
	@Redirect(method = "renderQuadsSmooth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BufferBuilder;putBrightness4(IIII)V"))
	private void redirBrightnessSmooth(BufferBuilder bufferBuilder, int vertex0, int vertex1, int vertex2, int vertex3) {
		if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBrightness()) {
			bufferBuilder.putBrightness4(15728640, 15728640, 15728640, 15728640);
		}
		else {
			bufferBuilder.putBrightness4(vertex0, vertex1, vertex2, vertex3);
		}
	}
	
	@Redirect(method = "renderQuadsSmooth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BufferBuilder;putColorMultiplier(FFFI)V"))
	private void redirBrightnessSmooth(BufferBuilder bufferBuilder, float red, float green, float blue, int vertexIndex) {
		//if (VisionModule.INSTANCE.getState() && VisionModule.INSTANCE.getBrightness()) {
		//	bufferBuilder.putColorMultiplier(1f, 1f, 1f, vertexIndex);
		//}
		//else {
		//	bufferBuilder.putColorMultiplier(red, green, blue, vertexIndex);
		//}
	}

    /*@Inject(method = "renderModel(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z", at = @At("HEAD"), cancellable = true)
    public void renderModel(IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, BufferBuilder buffer, boolean checkSides, long rand, CallbackInfoReturnable<Boolean> ci) {
        if(Raion.INSTANCE.getModuleManager().getModule(XrayModule.class).getState()) {
            boolean xray = !((XrayModule) Raion.INSTANCE.getModuleManager().getModule(XrayModule.class)).shouldXray(blockStateIn.getBlock());
            boolean returnvalue = renderModelSmooth(blockAccessIn, modelIn, blockStateIn, blockPosIn, buffer, xray, rand);
            ci.setReturnValue(returnvalue);
        }
    }*/
}
