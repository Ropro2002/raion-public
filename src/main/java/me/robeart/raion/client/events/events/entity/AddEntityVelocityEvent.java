package me.robeart.raion.client.events.events.entity;

import net.minecraft.entity.Entity;

import javax.annotation.Nullable;

public class AddEntityVelocityEvent {
	@Nullable
	public Entity entity;
	public double x;
	public double y;
	public double z;
	
	public AddEntityVelocityEvent(@Nullable Entity entity, double x, double y, double z) {
		this.entity = entity;
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
