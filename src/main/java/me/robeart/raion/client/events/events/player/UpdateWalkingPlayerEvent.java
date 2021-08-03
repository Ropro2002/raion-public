package me.robeart.raion.client.events.events.player;

import me.robeart.raion.client.events.EventCancellable;
import me.robeart.raion.client.events.EventStageable;

public class UpdateWalkingPlayerEvent extends EventCancellable {
	
	public UpdateWalkingPlayerEvent(EventStageable.EventStage stage) {
		super(stage);
	}
	
}
