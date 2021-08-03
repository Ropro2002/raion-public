package me.robeart.raion.client.events.events.render;

import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent {
	
	private float partialTicks;
	private ScaledResolution scaledResolution;
	
	public Render2DEvent(float partialTicks, ScaledResolution scaledResolution) {
		this.partialTicks = partialTicks;
		this.scaledResolution = scaledResolution;
	}
	
	public float getPartialTicks() {
		return this.partialTicks;
	}
	
	public void setPartialTicks(float partialTicks) {
		this.partialTicks = partialTicks;
	}
	
	public ScaledResolution getScaledResolution() {
		return this.scaledResolution;
	}
	
	public void setScaledResolution(ScaledResolution scaledResolution) {
		this.scaledResolution = scaledResolution;
	}
}
