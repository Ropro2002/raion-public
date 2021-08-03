package me.robeart.raion.mixin.common.block;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.block.IsLiquidSolidEvent;
import me.robeart.raion.client.events.events.entity.AddEntityVelocityEvent;
import me.robeart.raion.client.events.events.world.LiquidCollisionBBEvent;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLiquid.class)
public abstract class MixinBlockLiquid extends MixinBlock {
	
	@Inject(method = "canCollideCheck", at = @At("RETURN"), cancellable = true)
	private void canCollideWrapper(IBlockState state, boolean hitIfLiquid, CallbackInfoReturnable<Boolean> cir) {
		IsLiquidSolidEvent event = new IsLiquidSolidEvent(cir.getReturnValue());
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		cir.setReturnValue(event.getSolid());
	}
	
	@Shadow
	protected abstract Vec3d getFlow(IBlockAccess worldIn, BlockPos pos, IBlockState state);
	
	@Inject(method = "getCollisionBoundingBox", at = @At("RETURN"), cancellable = true)
	public void boundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> ci) {
		try {
			LiquidCollisionBBEvent liquidCollisionBBEvent = new LiquidCollisionBBEvent(ci.getReturnValue(), pos);
			Raion.INSTANCE.getEventManager().dispatchEvent(liquidCollisionBBEvent);
			
			if (liquidCollisionBBEvent.isCanceled())
				ci.setReturnValue(liquidCollisionBBEvent.getBoundingBox());
		}
		catch (Exception ignored) {
		}
	}
	
	@Inject(method = "modifyAcceleration", at = @At("HEAD"), cancellable = true)
	private void modifyAccelerationWrapper(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion, CallbackInfoReturnable<Vec3d> cir) {
		Vec3d pushMotion = getFlow(worldIn, pos, worldIn.getBlockState(pos));
		
		AddEntityVelocityEvent event = new AddEntityVelocityEvent(entityIn, pushMotion.x, pushMotion.y, pushMotion.z);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		
		cir.setReturnValue(new Vec3d(event.x, event.y, event.z));
		cir.cancel();
	}
}
