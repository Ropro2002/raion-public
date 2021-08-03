package me.robeart.raion.mixin.common.network.packet.server;

import me.robeart.raion.client.imixin.ISPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author cats
 */
@Mixin(SPacketPlayerPosLook.class)
public abstract class MixinSPacketPosLook implements ISPacketPlayerPosLook {
	
	@Accessor(value = "yaw")
	public abstract float getYaw();
	
	@Accessor(value = "yaw")
	public abstract void setYaw(float yaw);
	
	@Accessor(value = "pitch")
	public abstract float getPitch();
	
	@Accessor(value = "pitch")
	public abstract void setPitch(float pitch);
	
}
