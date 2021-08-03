package me.robeart.raion.mixin.common.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiScreen.class)
public class MixinGuiScreen {
	
	@Shadow
	public int width;
	
	@Shadow
	public int height;
	
	@Shadow
	public Minecraft mc;
	
}
