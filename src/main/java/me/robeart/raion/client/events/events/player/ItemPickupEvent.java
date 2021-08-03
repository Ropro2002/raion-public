package me.robeart.raion.client.events.events.player;

import net.minecraft.entity.Entity;

/**
 * @author Robeart
 */
public class ItemPickupEvent {
	
	private Entity entity;
	private int quantity;
	
	public ItemPickupEvent(Entity entity, int quantity) {
		this.entity = entity;
		this.quantity = quantity;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
