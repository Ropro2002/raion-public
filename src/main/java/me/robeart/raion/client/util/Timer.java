package me.robeart.raion.client.util;

public class Timer {
	
	private long time;
	
	public Timer() {
		this.time = -1L;
	}
	
	public boolean passed(double ms) {
		return System.currentTimeMillis() - this.time >= ms;
	}
	
	public boolean passed(int ms) {
		return System.currentTimeMillis() - this.time >= ms;
	}
	
	public void reset() {
		this.time = System.currentTimeMillis();
	}
	
	public long getTime() {
		return this.time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
}
