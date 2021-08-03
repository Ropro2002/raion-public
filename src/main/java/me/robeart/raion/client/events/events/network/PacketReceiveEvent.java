package me.robeart.raion.client.events.events.network;

import me.robeart.raion.client.events.EventCancellable;
import me.robeart.raion.client.events.EventStageable;
import net.minecraft.network.Packet;

public class PacketReceiveEvent extends EventCancellable {
	
	private Packet packet;
	
	public PacketReceiveEvent(EventStageable.EventStage stage, Packet packet) {
		super(stage);
		this.packet = packet;
	}
	
	public Packet getPacket() {
		return packet;
	}
	
}
