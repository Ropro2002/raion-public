package me.robeart.raion.client.events.events.render;

import me.robeart.raion.client.events.EventCancellable;
import net.minecraft.tileentity.TileEntityChest;

/**
 * @author Robeart
 */
public class RenderChestEvent extends EventCancellable {
	
	double x;
	double y;
	double z;
	private TileEntityChest entity;
	
	public RenderChestEvent(TileEntityChest entity, double x, double y, double z) {
		this.entity = entity;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public TileEntityChest getEntity() {
		return this.entity;
	}
	
	public void setEntity(TileEntityChest entity) {
		this.entity = entity;
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public double getZ() {
		return z;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
}
