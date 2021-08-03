package me.robeart.raion.client.module.combat;

import me.robeart.raion.client.events.events.player.OnUpdateEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.Timer;
import me.robeart.raion.client.value.BooleanValue;
import me.robeart.raion.client.value.DoubleValue;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author Robeart
 */
public class AutoArmorModule extends Module {
	
	public BooleanValue cursed = new BooleanValue("Cursed", false);
	public DoubleValue delay = new DoubleValue("Delay", 0, 0, 3, 0.1);
	
	private Timer timer = new Timer();
	
	public AutoArmorModule() {
		super("AutoArmor", "Automatically equips the best armor", Category.COMBAT);
	}
	
	@Listener
	private void onUpdate(OnUpdateEvent event) {
		if (mc.currentScreen instanceof GuiInventory) return;
		Item helm = mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem();
		Item chest = mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem();
		Item legs = mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem();
		Item feet = mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem();
		if (helm == Items.AIR) {
			int slot = findArmorSlot(EntityEquipmentSlot.HEAD);
			if (slot != -1) equiqArmor(slot);
		}
		if (chest == Items.AIR) {
			int slot = findArmorSlot(EntityEquipmentSlot.CHEST);
			if (slot != -1) equiqArmor(slot);
		}
		if (legs == Items.AIR) {
			int slot = findArmorSlot(EntityEquipmentSlot.LEGS);
			if (slot != -1) equiqArmor(slot);
		}
		if (feet == Items.AIR) {
			int slot = findArmorSlot(EntityEquipmentSlot.FEET);
			if (slot != -1) equiqArmor(slot);
		}
	}
	
	
	private void equiqArmor(int slot) {
		if (timer.passed(delay.getValue() / 1000)) {
			mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.QUICK_MOVE, mc.player);
			mc.playerController.updateController();
			timer.reset();
		}
	}
	
	
	private int findArmorSlot(EntityEquipmentSlot type) {
		int slot = -1;
		float damage = 0;
		for (int i = 9; i < 45; i++) {
			ItemStack item = mc.player.inventoryContainer.getSlot(i).getStack();
			if (item != null && item.getItem() instanceof ItemArmor) {
				ItemArmor armor = (ItemArmor) item.getItem();
				if (armor.armorType == type) {
					final float currentDamage = (armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, item));
					final boolean cursed = this.cursed.getValue() && (EnchantmentHelper.hasBindingCurse(item));
					if (currentDamage > damage && !cursed) {
						damage = currentDamage;
						slot = i;
					}
				}
			}
		}
		return slot;
	}
}
