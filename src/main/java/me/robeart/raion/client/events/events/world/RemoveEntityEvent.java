package me.robeart.raion.client.events.events.world;

import me.robeart.raion.client.events.EventCancellable;
import net.minecraft.entity.Entity;

/**
 * @author Robeart
 */
public class RemoveEntityEvent extends EventCancellable {
	
	private Entity entity;
	
	public RemoveEntityEvent(Entity entity) {
		this.entity = entity;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
}
