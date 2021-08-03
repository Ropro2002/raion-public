package me.robeart.raion.client.util.data;

/**
 * @author cookiedragon234 11/Nov/2019
 */
public class Vec2i {
	public int x;
	public int y;
	
	public Vec2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return x + ", " + y;
	}
}
