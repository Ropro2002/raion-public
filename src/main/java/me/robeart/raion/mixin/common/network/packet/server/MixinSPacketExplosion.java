package me.robeart.raion.mixin.common.network.packet.server;

import me.robeart.raion.client.imixin.ISPacketExplosion;
import net.minecraft.network.play.server.SPacketExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Robeart
 */
@Mixin(SPacketExplosion.class)
public abstract class MixinSPacketExplosion implements ISPacketExplosion {
	
	@Accessor(value = "motionX")
	public abstract void setMotionX(float motionX);
	
	@Accessor(value = "motionY")
	public abstract void setMotionY(float motionY);
	
	@Accessor(value = "motionZ")
	public abstract void setMotionZ(float motionZ);
	
}
