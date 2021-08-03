package me.robeart.raion.client.events.events.client;

import me.robeart.raion.client.module.Module;

public class ToggleEvent {
	
	private Module module;
	
	public ToggleEvent(Module module) {
		this.module = module;
	}
	
	public Module getModule() {
		return module;
	}
}
