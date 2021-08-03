package me.robeart.raion.mixin.common.block;

import me.robeart.raion.client.module.player.BetterPortalsModule;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author cookiedragon234 08/Jun/2020
 */
@Mixin(BlockPortal.class)
public class MixinBlockPortal {
	private static final AxisAlignedBB EMPTY_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
	
	@Inject(method = "getBoundingBox", at = @At("HEAD"), cancellable = true)
	private void injectBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> cir) {
		if (BetterPortalsModule.INSTANCE.getState() && BetterPortalsModule.INSTANCE.getNoHitbox()) {
			cir.setReturnValue(EMPTY_AABB);
		}
	}
	
	@Redirect(method = "randomDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FFZ)V"))
	private void redirectPlaySound(World world, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
		if (BetterPortalsModule.INSTANCE.getState() && BetterPortalsModule.INSTANCE.getNoSound()) {
			return;
		}
		else {
			world.playSound(x, y, z, soundIn, category, volume, pitch, distanceDelay);
		}
	}
	
	@Redirect(method = "randomDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnParticle(Lnet/minecraft/util/EnumParticleTypes;DDDDDD[I)V"))
	private void redirectSpawnParticle(World world, EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
		if (BetterPortalsModule.INSTANCE.getState() && BetterPortalsModule.INSTANCE.getNoParticles()) {
			return;
		}
		else {
			world.spawnParticle(particleType, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
		}
	}
}
