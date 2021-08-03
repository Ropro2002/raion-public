package me.robeart.raion.client.events.events.entity;

import me.robeart.raion.client.events.EventCancellable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

/**
 * @author cats
 * @since 19 Mar 2020
 */
public class EntityLivingBaseTravelEvent extends EventCancellable {
	private EntityLivingBase entity;
	
	public EntityLivingBaseTravelEvent(Entity entity) {
		this.entity = (EntityLivingBase) entity;
	}
	
	public EntityLivingBaseTravelEvent(EntityLivingBase entity) {
		this.entity = entity;
	}
	
	public EntityLivingBase getEntity() {
		return this.entity;
	}
}
