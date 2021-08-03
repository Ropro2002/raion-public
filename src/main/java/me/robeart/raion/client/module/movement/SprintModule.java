package me.robeart.raion.client.module.movement;

import me.robeart.raion.client.events.EventStageable;
import me.robeart.raion.client.events.events.player.UpdateWalkingPlayerEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.value.ListValue;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.Arrays;

/**
 * @author cookiedragon234
 */
public class SprintModule extends Module {
	private final ListValue mode = new ListValue("Mode", "Rage", Arrays.asList("Rage", "Legit"));
	
	public SprintModule() {
		super("Sprint", "Automatically sprints", Category.MOVEMENT);
	}
	
	@Listener
	private void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
		if (event.getStage() == EventStageable.EventStage.PRE) {
			if (mode.getValue().equals("Rage")) {
				if (mc.player.movementInput.moveForward != 0 || mc.player.movementInput.moveStrafe != 0) {
					mc.player.setSprinting(true);
				}
			}
			else {
				if (mc.player.movementInput.moveForward >= 0.8F && mc.player.getFoodStats().getFoodLevel() > 6.0f) {
					mc.player.setSprinting(true);
				}
			}
		}
	}
}
