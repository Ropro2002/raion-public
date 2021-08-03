package me.robeart.raion.mixin.common.network.packet.client;

import me.robeart.raion.client.imixin.ICPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Robeart
 */
@Mixin(CPacketPlayer.class)
public abstract class MixinCPacketPlayer implements ICPacketPlayer {
	
	@Accessor(value = "yaw")
	public abstract void setYaw(float yaw);
	
	@Accessor(value = "pitch")
	public abstract void setPitch(float pitch);
	
	@Accessor(value = "x")
	public abstract void setX(double x);
	
	@Accessor(value = "y")
	public abstract void setY(double y);
	
	@Accessor(value = "z")
	public abstract void setZ(double z);
	
	@Accessor(value = "onGround")
	public abstract void setOnGround(boolean onGround);
	
}
