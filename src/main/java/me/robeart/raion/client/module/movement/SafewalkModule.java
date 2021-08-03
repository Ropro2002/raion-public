package me.robeart.raion.client.module.movement;

import me.robeart.raion.client.events.events.entity.ShouldWalkOffEdgeEvent;
import me.robeart.raion.client.module.Module;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author Robeart
 */
public class SafewalkModule extends Module {
	
	public SafewalkModule() {
		super("SafeWalk", "Stops you from walking off of the edge of a block", Category.MOVEMENT);
	}
	
	@Listener
	public void shouldWalkOfEdge(ShouldWalkOffEdgeEvent event) {
		event.setCanceled(true);
	}
	
}
