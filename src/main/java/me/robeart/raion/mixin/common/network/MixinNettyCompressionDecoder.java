package me.robeart.raion.mixin.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.robeart.raion.client.module.misc.NoCompressionKick;
import me.robeart.raion.client.util.ChatUtils;
import net.minecraft.network.NettyCompressionDecoder;
import net.minecraft.network.PacketBuffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * @author cats
 */
@Mixin(NettyCompressionDecoder.class)
public abstract class MixinNettyCompressionDecoder extends ByteToMessageDecoder {
	
	@Shadow
	private int threshold;
	
	@Final
	@Shadow
	private Inflater inflater;
	
	/**
	 * @author cats
	 */
	@Inject(method = "decode", at = @At("HEAD"), cancellable = true)
	private void decode(ChannelHandlerContext p_decode_1_, ByteBuf p_decode_2_, List<Object> p_decode_3_, CallbackInfo ci) throws DataFormatException {
		if (NoCompressionKick.INSTANCE.getState()) {
			ci.cancel();
			if (p_decode_2_.readableBytes() != 0) {
				PacketBuffer packetbuffer = new PacketBuffer(p_decode_2_);
				int i = packetbuffer.readVarInt();
				
				if (i == 0) {
					p_decode_3_.add(packetbuffer.readBytes(packetbuffer.readableBytes()));
				}
				else {
					if (i < this.threshold) {
						ChatUtils.message("Exception stopped from packet of size " + i);
					}
					
					if (i > 2097152) {
						ChatUtils.message("Exception stopped from packet of size " + i);
					}
					
					byte[] abyte = new byte[packetbuffer.readableBytes()];
					packetbuffer.readBytes(abyte);
					this.inflater.setInput(abyte);
					byte[] abyte1 = new byte[i];
					this.inflater.inflate(abyte1);
					p_decode_3_.add(Unpooled.wrappedBuffer(abyte1));
					this.inflater.reset();
				}
			}
		}
	}
}
