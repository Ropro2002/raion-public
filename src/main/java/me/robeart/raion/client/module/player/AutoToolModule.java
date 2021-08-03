package me.robeart.raion.client.module.player;

import me.robeart.raion.client.events.events.player.SwingArmEvent;
import me.robeart.raion.client.imixin.IItemTool;
import me.robeart.raion.client.module.Module;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.math.RayTraceResult;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;


/**
 * @author cookiedragon234 13/Nov/2019
 */
public class AutoToolModule extends Module {
	public AutoToolModule() {
		super("AutoTool", "Automatically uses the best tool ", Category.PLAYER);
	}
	
	private static double getToolDamageSpeed(ItemStack itemStack, IBlockState block) {
		double speed = itemStack.getDestroySpeed(block);
		
		if (speed > 1) {
			int efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, itemStack);
			
			if (efficiency > 0) {
				speed += (efficiency * efficiency) + 1;
			}
		}
		return speed;
	}
	
	private static double getItemDamage(ItemStack itemStack) {
		double thisDamage = -1;
		
		if (itemStack.getItem() instanceof ItemTool) {
			thisDamage = ((IItemTool) itemStack.getItem()).getAttackDamage();
		}
		else if (itemStack.getItem() instanceof ItemSword) {
			//TODO fix this
			//thisDamage = ((ItemSword)itemStack.getItem()).attackDamage;
		}
		
		thisDamage += EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED);
		
		return thisDamage;
	}
	
	@Listener
	private void onArmSwing(SwingArmEvent event) {
		if (mc.objectMouseOver != null && mc.gameSettings.keyBindAttack.isKeyDown()) {
			int slot = mc.player.inventory.currentItem;
			ItemStack originalItem = mc.player.inventory.getCurrentItem();
			
			// If player is attacking an entity equip the best weapon to deal the most damage
			if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
				double currentBestDamage = getItemDamage(originalItem);
				
				for (int i = 0; i < 9; i++) {
					ItemStack stack = mc.player.inventory.getStackInSlot(i);
					
					if (!stack.isEmpty()) {
						double thisDamage = -1;
						{
							if (stack.getItem() instanceof ItemTool) {
								ItemTool tool = (ItemTool) stack.getItem();
								thisDamage = ((IItemTool) tool).getAttackDamage();
							}
							else if (stack.getItem() instanceof ItemSword) {
								ItemSword sword = (ItemSword) stack.getItem();
								thisDamage = ((IItemTool) sword).getAttackDamage();
							}
							thisDamage += EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
							
							if (thisDamage > currentBestDamage) {
								slot = i;
								currentBestDamage = thisDamage;
							}
						}
					}
				}
			}
			else if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
				IBlockState block = mc.world.getBlockState(mc.objectMouseOver.getBlockPos());
				
				double currentBestSpeed = getToolDamageSpeed(originalItem, block);
				
				if (block.getBlock() instanceof BlockAir || block.getMaterial().isLiquid()) return;
				
				for (int i = 0; i < 9; i++) {
					ItemStack stack = mc.player.inventory.getStackInSlot(i);
					
					if (!stack.isEmpty()) {
						double speed = getToolDamageSpeed(stack, block);
						
						if (speed > currentBestSpeed) {
							currentBestSpeed = speed;
							slot = i;
						}
					}
				}
			}
			
			mc.player.inventory.currentItem = slot;
		}
	}
}
