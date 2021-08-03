package me.robeart.raion.client.events;

public class EventStageable {
	private EventStageable.EventStage stage;
	
	public EventStageable() {
	}
	
	public EventStageable(EventStageable.EventStage stage) {
		this.stage = stage;
	}
	
	public EventStageable.EventStage getStage() {
		return this.stage;
	}
	
	public void setStage(EventStageable.EventStage stage) {
		this.stage = stage;
	}
	
	public enum EventStage {
		PRE,
		POST
	}
}
