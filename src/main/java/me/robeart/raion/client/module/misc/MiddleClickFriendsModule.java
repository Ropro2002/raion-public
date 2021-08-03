package me.robeart.raion.client.module.misc;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class MiddleClickFriendsModule extends Module {
	
	private boolean down;
	
	public MiddleClickFriendsModule() {
		super("ClickFriends", "Adds people as friends when clicking on them using the middle mouse button", Category.MISC);
	}
	
	@Listener
	private void onUpdate(OnUpdateEvent event) {
		if (mc.currentScreen == null) {
			if (mc.gameSettings.keyBindPickBlock.isKeyDown()) {
				if (!down) {
					RayTraceResult result = mc.objectMouseOver;
					if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY) {
						Entity entity = result.entityHit;
						if (entity != null && entity instanceof EntityPlayer) {
							boolean friend = Raion.INSTANCE.getFriendManager().isFriend((EntityPlayer) entity);
							String name = entity.getName();
							if (friend) {
								if (Raion.INSTANCE.getFriendManager().remove(name))
									ChatUtils.message("Removed " + name + " from your friendslist");
								else ChatUtils.message("Can't find " + name + " in your friendslist");
							}
							else {
								if (Raion.INSTANCE.getFriendManager().add(name))
									ChatUtils.message("Added " + name + " to your friendslist");
								else ChatUtils.message(name + "is already in your friendslist");
							}
						}
					}
				}
				down = true;
			}
			else {
				down = false;
			}
		}
	}
	
}
