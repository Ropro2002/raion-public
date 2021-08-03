package me.robeart.raion.client.module.render;

import me.robeart.raion.client.events.events.render.RenderBossBarEvent;
import me.robeart.raion.client.module.Module;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author cats
 */
public class NoBossBarModule extends Module {
	
	public NoBossBarModule() {
		super("NoBossBar", "Hides the boss health bar on entities like withers and enderdragons", Category.RENDER);
	}
	
	@Listener
	public void onRenderBossBar(RenderBossBarEvent event) {
		event.setCanceled(true);
	}
}
