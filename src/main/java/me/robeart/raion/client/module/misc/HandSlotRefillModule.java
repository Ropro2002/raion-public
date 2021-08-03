package me.robeart.raion.client.module.misc;

import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.Timer;
import me.robeart.raion.client.value.IntValue;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author cats
 * @since 19 Mar 2020
 * I don't think this works just yet
 */
public class HandSlotRefillModule extends Module {

	public IntValue delay = new IntValue("Click Delay", 50, 0, 500, 1);
	private Timer timer = new Timer();
	private int movePhase = 0;
	private int slot;

	public HandSlotRefillModule() {
		super("HandRefill", "Automatically refills your hand when it's low on items", Category.MISC);
	}

	@Listener
	public void onPlayerUpdate(OnUpdateEvent event) {
		final ItemStack heldItem = mc.player.getHeldItemMainhand();
		if (!this.timer.passed(this.delay.getValue() * this.movePhase)) return;

		switch (this.movePhase) {
			case 0: {
				if (!(heldItem.getCount() < (heldItem.getMaxStackSize() / 2))) return;
				this.slot = getSlotOfNonHeldItem(heldItem.getItem());
				if ((this.slot == -1 || mc.player.inventory.getStackInSlot(this.slot).getItem() == Items.AIR)
					|| mc.currentScreen != null) return;
				this.movePhase = 1;
				this.timer.reset();
				break;
			}

			case 1: {
				mc.playerController.windowClick(mc.player.inventoryContainer.windowId, this.slot, 0, ClickType.PICKUP, mc.player);
				mc.playerController.updateController();

				this.movePhase = 2;
				break;
			}

			case 2: {
				mc.playerController.windowClick(mc.player.inventoryContainer.windowId, mc.player.inventory.currentItem + 36, 0, ClickType.PICKUP, mc.player);
				mc.playerController.updateController();
				this.movePhase = 3;
				break;
			}

			case 3: {
				mc.playerController.windowClick(mc.player.inventoryContainer.windowId, this.slot, 0, ClickType.PICKUP, mc.player);
				mc.playerController.updateController();
				this.movePhase = 0;
				this.timer.reset();
				break;
			}
		}

	}

	private int getSlotOfNonHeldItem(Item input) {
		for (int i = 0; i < 36; i++) {
			ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
			if (itemStack.getItem() == input &&
				(itemStack.getDisplayName()
					.equals(mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getDisplayName()) ||
					(itemStack.getDisplayName()
						.isEmpty() && mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem)
						.getDisplayName()
						.isEmpty()))
				&& mc.player.inventory.currentItem != i) return i < 9 ? i + 36 : i;
		}
		return -1;
	}
}
