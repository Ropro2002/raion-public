package me.robeart.raion.client.module.combat;

import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.minecraft.MinecraftUtils;
import me.robeart.raion.client.value.BooleanValue;
import me.robeart.raion.client.value.FloatValue;
import net.minecraft.util.text.TextComponentString;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author Robeart
 */
public class AutoDisconnectModule extends Module {
	
	public FloatValue health = new FloatValue("Health", 4.0f, 1f, 20f, 1f);
	public BooleanValue disable = new BooleanValue("Disable on DC", true);
	
	public AutoDisconnectModule() {
		super("AutoDisconnect", "Automatically disconnects from the server when on set health", Category.COMBAT);
	}
	
	@Listener
	private void onUpdate(OnUpdateEvent event) {
		if (mc.player.getHealth() <= health.getValue() && mc.player.getHealth() != 0 && !mc.player.isDead) {
			MinecraftUtils.quickLogout();
			mc.getConnection().getNetworkManager().closeChannel(new TextComponentString("Kicked by AutoDisconnect!"));
			if (disable.getValue()) this.toggle();
		}
	}
	
	
}
