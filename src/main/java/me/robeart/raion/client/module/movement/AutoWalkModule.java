package me.robeart.raion.client.module.movement;

import me.robeart.raion.client.events.events.client.GetKeyStateEvent;
import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.value.BooleanValue;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class AutoWalkModule extends Module {
	
	public BooleanValue sprint = new BooleanValue("Sprint", true);
	
	public AutoWalkModule() {
		super("AutoWalk", "Automatically walks for you", Category.MOVEMENT);
	}
	
	@Listener
	public void onUpdate(OnUpdateEvent event) {
		if (sprint.getValue()) mc.player.setSprinting(true);
	}
	
	@Listener
	private void onGetKeyState(GetKeyStateEvent event) {
		try {
			if (event.keyBinding == mc.gameSettings.keyBindForward) event.value = true;
		}
		catch (Exception ignored) {
		}
	}
}
