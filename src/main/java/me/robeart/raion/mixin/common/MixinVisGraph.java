package me.robeart.raion.mixin.common;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.render.SetOpaqueCubeEvent;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VisGraph.class)
public class MixinVisGraph {
	
	@Inject(method = "setOpaqueCube", at = @At("HEAD"), cancellable = true)
	public void setOpaqueCube(BlockPos pos, CallbackInfo ci) {
		SetOpaqueCubeEvent event = new SetOpaqueCubeEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
}
