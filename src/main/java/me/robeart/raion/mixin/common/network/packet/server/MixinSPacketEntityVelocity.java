package me.robeart.raion.mixin.common.network.packet.server;

import me.robeart.raion.client.imixin.ISPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Robeart
 */
@Mixin(SPacketEntityVelocity.class)
public abstract class MixinSPacketEntityVelocity implements ISPacketEntityVelocity {
	
	@Accessor(value = "motionX")
	public abstract void setMotionX(int motionX);
	
	@Accessor(value = "motionY")
	public abstract void setMotionY(int motionY);
	
	@Accessor(value = "motionZ")
	public abstract void setMotionZ(int motionZ);
	
	
}
