package me.robeart.raion.client.events.events.player;

import me.robeart.raion.client.events.EventCancellable;
import net.minecraft.entity.Entity;

public class ApplyCollisionEvent extends EventCancellable {
	
	Entity entity;
	
	public ApplyCollisionEvent(Entity entity) {
		this.entity = entity;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
}
