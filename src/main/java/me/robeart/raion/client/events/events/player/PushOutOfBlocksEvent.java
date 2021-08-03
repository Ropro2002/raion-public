package me.robeart.raion.client.events.events.player;

import me.robeart.raion.client.events.EventCancellable;

public class PushOutOfBlocksEvent extends EventCancellable {
	public double x, y, z;
	
	public PushOutOfBlocksEvent(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
