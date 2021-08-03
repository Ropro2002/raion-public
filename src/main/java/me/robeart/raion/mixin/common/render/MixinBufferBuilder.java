package me.robeart.raion.mixin.common.render;

import net.minecraft.client.renderer.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder {
	
	@Accessor
	public abstract boolean isIsDrawing();

    /*@Redirect(method = "putColorMultiplier", at = @At(value = "INVOKE", remap = false, target = "java/nio/IntBuffer.put(II)Ljava/nio/IntBuffer;"))
    private IntBuffer onPutColorMultiplier(IntBuffer a, int a2, int a3) {
        if (Raion.INSTANCE.getModuleManager().getModule(XrayModule.class).getState()) {
            a3 = ((XrayModule) Raion.INSTANCE.getModuleManager().getModule(XrayModule.class)).opacity.getValue() << 24 | a3 & 0xFFFFFF;
        }
        return a.put(a2, a3);
    }*/
	
}
