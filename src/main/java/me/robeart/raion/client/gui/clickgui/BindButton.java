package me.robeart.raion.client.gui.clickgui;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.module.ClickGuiModule;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.Interpolation;
import net.minecraft.client.Minecraft;

import static me.robeart.raion.client.util.Utils.getRgb;

public class BindButton {
	private static final Minecraft mc = Minecraft.getMinecraft();
	public Button button;
	public float x;
	public float y;
	public Module module;
	private int colour;
	
	public BindButton(Button button, float x, float y, Module module) {
		this.button = button;
		this.x = x;
		this.y = y;
		this.module = module;
	}
	
	public int getColour(boolean hovered) {
		int desired;
		if (hovered) {
			desired = getRgb(77, 77, 77, 100);
		}
		else {
			desired = getRgb(0, 0, 0, 80);
		}
		colour = Interpolation.cinterpTo(colour, desired, mc.getRenderPartialTicks(), ClickGuiModule.INSTANCE.getColourSpeed());
		return colour;
	}
	
	public void draw(float mouseX, float mouseY) {
		Raion.INSTANCE.getGui().getTheme().bindButtonDraw(this, mouseX, mouseY, this.button);
	}
	
	public void mouseClicked(float mouseX, float mouseY, int state) {
		Raion.INSTANCE.getGui().getTheme().bindButtonMouseClicked(this, mouseX, mouseY, state, this.button);
	}
	
}
