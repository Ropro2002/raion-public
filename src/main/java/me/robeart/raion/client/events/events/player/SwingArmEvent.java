package me.robeart.raion.client.events.events.player;

import me.robeart.raion.client.events.EventCancellable;
import net.minecraft.util.EnumHand;

public class SwingArmEvent extends EventCancellable {
	
	private EnumHand hand;
	
	public SwingArmEvent(EnumHand hand) {
		this.hand = hand;
	}
	
	public EnumHand getHand() {
		return this.hand;
	}
	
	public void setHand(EnumHand hand) {
		this.hand = hand;
	}
}
