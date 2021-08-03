package me.robeart.raion.mixin.common.render;

import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * @author cookiedragon234 08/Jun/2020
 */
@Mixin(RenderGlobal.class)
public interface IMixinRenderGlobal {
	@Accessor
	Map<Integer, DestroyBlockProgress> getDamagedBlocks();
}
