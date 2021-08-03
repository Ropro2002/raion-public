package me.robeart.raion.mixin.common.network.packet.client;

import me.robeart.raion.client.imixin.ICPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Robeart 25/07/2020
 */
@Mixin(CPacketPlayerTryUseItemOnBlock.class)
public abstract class MixinCPacketPlayerPlayerTryUseItemOnBlock implements ICPacketPlayerTryUseItemOnBlock {

    @Accessor(value = "placedBlockDirection")
    public abstract void setDirection(EnumFacing placedBlockDirection);

}
