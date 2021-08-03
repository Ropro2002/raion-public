package me.robeart.raion.client.events.events.entity;

import me.robeart.raion.client.events.EventCancellable;

public class HorseJumpStrengthEvent extends EventCancellable {
	
	private double strength;
	
	public HorseJumpStrengthEvent(double strength) {
		this.strength = strength;
	}
	
	public double getStrength() {
		return strength;
	}
	
	public void setStrength(double strength) {
		this.strength = strength;
	}
}
