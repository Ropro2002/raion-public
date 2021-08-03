package me.robeart.raion.client.gui.clickgui;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.module.ClickGuiModule;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.Interpolation;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class CategoryButton {
	private static final Minecraft mc = Minecraft.getMinecraft();
	public float x;
	public float y;
	public boolean enabled;
	public Module.Category category;
	public ResourceLocation image;
	public CategoryPanel categoryPanel;
	public MainPanel mainPanel;
	
	private int colour;
	
	public CategoryButton(Module.Category category, float x, float y, ResourceLocation image, MainPanel mainPanel) {
		this.category = category;
		this.x = x;
		this.y = y;
		this.enabled = false;
		this.image = image;
		this.mainPanel = mainPanel;
		mainPanel.theme.categoryButtonConstructor(this, this.mainPanel);
	}
	
	public int getColour() {
		int desired;
		if (enabled) {
			desired = 0xFFFFFFFF;
		}
		else {
			desired = 0xFFa7a7a8;
		}
		colour = Interpolation.cinterpTo(colour, desired, mc.getRenderPartialTicks(), ClickGuiModule.INSTANCE.getColourSpeed());
		return colour;
	}
	
	public void draw(float mouseX, float mouseY) {
		mainPanel.theme.categoryButtonDraw(this, mouseX, mouseY, this.image);
	}
	
	public void mouseClicked(float mouseX, float mouseY, int button) {
		mainPanel.theme.categoryButtonMouseClicked(this, this.mainPanel, mouseX, mouseY, button);
	}
	
	public boolean keyPressed(int key) {
		return Raion.INSTANCE.getGui().getTheme().categoryButtonKeyPress(this, key);
	}
	
}
