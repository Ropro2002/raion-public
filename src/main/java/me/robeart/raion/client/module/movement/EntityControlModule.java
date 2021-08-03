package me.robeart.raion.client.module.movement;

import me.robeart.raion.client.events.events.entity.AbstractHorseSaddledEvent;
import me.robeart.raion.client.events.events.entity.CanBeSteeredEvent;
import me.robeart.raion.client.events.events.entity.PigTravelEvent;
import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.imixin.IEntityPlayerSP;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.value.BooleanValue;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class EntityControlModule extends Module {
	
	public BooleanValue ai = new BooleanValue("Pig AI", true);
	public BooleanValue jump = new BooleanValue("Jump Strength", true);
	
	public EntityControlModule() {
		super("EntityControl", "Allows you to control a rideable entity", Module.Category.MOVEMENT);
	}
	
	@Listener
	private void onUpdate(OnUpdateEvent event) {
		if (jump.getValue()) {
			IEntityPlayerSP player = (IEntityPlayerSP) mc.player;
			player.setHorseJumpPower(1.0f);
		}
	}
	
	@Listener
	private void canBeSteered(CanBeSteeredEvent event) {
		event.setCanceled(true);
	}
	
	@Listener
	private void travel(PigTravelEvent event) {
		if (ai.getValue()) event.setCanceled(true);
	}
	
	@Listener
	private void isSaddled(AbstractHorseSaddledEvent event) {
		event.setCanceled(true);
	}
	
}
