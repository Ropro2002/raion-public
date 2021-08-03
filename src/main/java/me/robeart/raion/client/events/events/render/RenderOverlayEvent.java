package me.robeart.raion.client.events.events.render;

import me.robeart.raion.client.events.EventCancellable;

/**
 * @author Robeart
 */
public class RenderOverlayEvent extends EventCancellable {
	
	private OverlayType type;
	
	public RenderOverlayEvent(OverlayType type) {
		this.type = type;
	}
	
	public OverlayType getType() {
		return type;
	}
	
	public void setType(OverlayType type) {
		this.type = type;
	}
	
	public enum OverlayType {
		ITEM,
		LIQUID,
		FIRE
	}
	
}
