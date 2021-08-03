package me.robeart.raion.client.events.events.player;

import me.robeart.raion.client.events.EventCancellable;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

/**
 * @author Robeart
 */
public class LocateCapeEvent extends EventCancellable {
	
	ResourceLocation resourceLocation;
	UUID uuid;
	
	public LocateCapeEvent(UUID uuid) {
		this.uuid = uuid;
		this.resourceLocation = null;
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	
	public ResourceLocation getResourceLocation() {
		return resourceLocation;
	}
	
	public void setResourceLocation(ResourceLocation resourceLocation) {
		this.resourceLocation = resourceLocation;
	}
}
