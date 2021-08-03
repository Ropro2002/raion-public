package me.robeart.raion.mixin.common.item;

import me.robeart.raion.client.imixin.IItemTool;
import net.minecraft.item.ItemTool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Robeart
 */
@Mixin(ItemTool.class)
public abstract class MixinItemTool implements IItemTool {
	
	@Accessor(value = "attackDamage")
	public abstract float getAttackDamage();
	
}
