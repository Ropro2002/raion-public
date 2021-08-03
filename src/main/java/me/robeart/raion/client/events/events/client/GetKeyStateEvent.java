package me.robeart.raion.client.events.events.client;

import net.minecraft.client.settings.KeyBinding;

/**
 * @author cookiedragon234 12/Nov/2019
 */
public class GetKeyStateEvent {
	public final KeyBinding keyBinding;
	public boolean value;
	
	public GetKeyStateEvent(KeyBinding keyBinding, boolean value) {
		this.keyBinding = keyBinding;
		this.value = value;
	}
}
