package me.robeart.raion.client.module.player;

import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.imixin.IMinecraft;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.value.IntValue;
import me.robeart.raion.client.value.ListValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemExpBottle;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.Arrays;

/**
 * @author cookiedragon234
 */
public class FastPlaceModule extends Module {
	private final ListValue whitelist = new ListValue("Whitelist", "All", Arrays.asList("All", "Exp Only", "Crystals Only", "Exp and Crystals only"));
	private final IntValue delay = new IntValue("Delay", 0, 0, 4, 1);
	public FastPlaceModule() {
		super("FastPlace", "Removes the delay between placing or using items", Category.PLAYER);
	}
	
	/**
	 * @param whitelist      The user provided whitelist
	 * @param holdingExp     Whether the player is holding an EXP bottle
	 * @param holdingCrystal Whether the player is holding an end crystal
	 * @return Whether we should activate fast place based on the user provided whitelist
	 */
	private static boolean shouldFastPlace(ListValue whitelist, boolean holdingExp, boolean holdingCrystal) {
		switch (whitelist.getValue()) {
			case "All":
				return true;
			case "Exp Only":
				if (holdingExp)
					return true;
				break;
			case "Crystals Only":
				if (holdingCrystal)
					return true;
				break;
			case "Exp and Crystals only":
				if (holdingCrystal || holdingExp)
					return true;
				break;
		}
		return false;
	}
	
	@Listener
	public void onUpdate(OnUpdateEvent event) {
		// Player's currently held item
		Item item = mc.player.inventory.getCurrentItem().getItem();
		
		boolean holdingExp = item instanceof ItemExpBottle;
		boolean holdingCrystal = item instanceof ItemEndCrystal;
		
		if (shouldFastPlace(whitelist, holdingExp, holdingCrystal)) {
			if (delay.getValue() < ((IMinecraft) mc).getRightClickDelayTimer())
				((IMinecraft) mc).setRightClickDelayTimer(delay.getValue());
		}
	}
}
