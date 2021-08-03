package me.robeart.raion.mixin.common.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.network.HandleDisconnectionEvent;
import me.robeart.raion.client.events.events.network.PacketExceptionEvent;
import me.robeart.raion.client.events.events.network.PacketReceiveEvent;
import me.robeart.raion.client.events.events.network.PacketSendEvent;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager extends SimpleChannelInboundHandler {
	@Inject(method = "handleDisconnection", at = @At("HEAD"), cancellable = true)
	private void injectDisconnection(CallbackInfo ci) {
		HandleDisconnectionEvent event = new HandleDisconnectionEvent();
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
	private void onSendPacketPre(Packet<?> packet, CallbackInfo ci) {
		PacketSendEvent event = new PacketSendEvent(EventStageable.EventStage.PRE, packet, this);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("TAIL"), cancellable = true)
	private void onSendPacketPost(Packet<?> packet, CallbackInfo ci) {
		PacketSendEvent event = new PacketSendEvent(EventStageable.EventStage.POST, packet, this);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
	private void onChannelReadPre(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
		PacketReceiveEvent event = new PacketReceiveEvent(EventStageable.EventStage.PRE, packet);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Inject(method = "channelRead0", at = @At("TAIL"), cancellable = true)
	private void onChannelReadPost(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
		PacketReceiveEvent event = new PacketReceiveEvent(EventStageable.EventStage.POST, packet);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Inject(method = "exceptionCaught", at = @At("HEAD"), cancellable = true)
	private void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable, CallbackInfo ci) {
		PacketExceptionEvent event = new PacketExceptionEvent(EventStageable.EventStage.POST);
		Raion.INSTANCE.getEventManager().dispatchEvent(event);
		if (event.isCanceled()) ci.cancel();
	}
}
