package me.robeart.raion.client.events.events.network;

import io.netty.channel.SimpleChannelInboundHandler;
import me.robeart.raion.client.events.EventCancellable;
import me.robeart.raion.client.events.EventStageable;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public class PacketSendEvent extends EventCancellable {
	
	private Packet packet;
	private NetworkManager networkManager;
	
	public PacketSendEvent(EventStageable.EventStage stage, Packet packet, SimpleChannelInboundHandler networkManager) {
		super(stage);
		this.packet = packet;
		this.networkManager = (NetworkManager) networkManager;
	}
	
	public PacketSendEvent(EventStageable.EventStage stage, Packet packet, NetworkManager networkManager) {
		super(stage);
		this.packet = packet;
		this.networkManager = networkManager;
	}
	
	public Packet getPacket() {
		return packet;
	}
	
	public NetworkManager getNetworkManager() {
		return networkManager;
	}
}
