package me.robeart.raion.client.events.events.player;

import me.robeart.raion.client.events.EventCancellable;
import net.minecraft.entity.MoverType;

public class MoveEvent extends EventCancellable {
	
	private MoverType moverType;
	private double x;
	private double y;
	private double z;
	
	public MoveEvent(MoverType moverType, double x, double y, double z) {
		this.moverType = moverType;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public MoverType getMoverType() {
		return this.moverType;
	}
	
	public void setMoverType(MoverType moverType) {
		this.moverType = moverType;
	}
	
	public double getX() {
		return this.x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public double getZ() {
		return this.z;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
}
