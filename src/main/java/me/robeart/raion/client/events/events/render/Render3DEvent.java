package me.robeart.raion.client.events.events.render;

public class Render3DEvent {
	private float partialTicks;
	private long pass;
	
	public Render3DEvent(float partialTicks, long finishTimeNano) {
		this.partialTicks = partialTicks;
		this.pass = finishTimeNano;
	}
	
	public float getPartialTicks() {
		return this.partialTicks;
	}
	
	public void setPartialTicks(float partialTicks) {
		this.partialTicks = partialTicks;
	}
	
	public long getPass() {
		return this.pass;
	}
	
	public void setPass(long pass) {
		this.pass = pass;
	}
}
