package me.robeart.raion.client.module.player;

import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.imixin.IMinecraft;
import me.robeart.raion.client.imixin.ITimer;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.value.FloatValue;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class TimerModule extends Module {
	
	public FloatValue speed = new FloatValue("Speed", 2.0f, 0.1f, 50.0f, 0.1f);
	
	public TimerModule() {
		super("Timer", "Speeds up your whole game", Category.PLAYER);
	}
	
	@Override
	public void onDisable() {
		((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f);
	}
	
	@Listener
	private void onUpdate(OnUpdateEvent event) {
		((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f / speed.getValue());
	}
}
