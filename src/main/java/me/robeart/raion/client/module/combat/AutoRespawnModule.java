package me.robeart.raion.client.module.combat;

import me.robeart.raion.client.events.events.render.DisplayGuiScreenEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.ChatUtils;
import me.robeart.raion.client.util.MathUtils;
import net.minecraft.client.gui.GuiGameOver;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author Robeart
 */
public class AutoRespawnModule extends Module {
	
	public AutoRespawnModule() {
		super("AutoRespawn", "Automatically respawn upon death", Category.COMBAT);
	}
	
	@Listener
	private void onDisplayGuiScreen(DisplayGuiScreenEvent event) {
		if (event.getGuiScreen() instanceof GuiGameOver) {
			ChatUtils.message("Died at: " + MathUtils.round(mc.player.posX, 0) + " " + MathUtils.round(mc.player.posY, 0) + " " + MathUtils
				.round(mc.player.posZ, 0));
			mc.player.respawnPlayer();
		}
	}
}
