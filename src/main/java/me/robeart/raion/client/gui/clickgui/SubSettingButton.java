package me.robeart.raion.client.gui.clickgui;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.module.ClickGuiModule;
import me.robeart.raion.client.util.Interpolation;
import me.robeart.raion.client.value.ColorValue;
import me.robeart.raion.client.value.Value;
import net.minecraft.client.Minecraft;

import static me.robeart.raion.client.util.Utils.getRgb;

public class SubSettingButton {
	private static final Minecraft mc = Minecraft.getMinecraft();
	public SettingButton settingButton;
	public float y;
	public boolean mouseDown;
	public String name;
	public Value setting;
	public ColorValue colorValue;
	private int colour;
	
	public SubSettingButton(SettingButton settingButton, float y, Value setting) {
		this.settingButton = settingButton;
		this.y = y;
		this.setting = setting;
		this.name = setting.getName();
		this.colorValue = null;
	}
	
	public SubSettingButton(SettingButton settingButton, float y, Value setting, ColorValue colorValue) {
		this.settingButton = settingButton;
		this.y = y;
		this.setting = setting;
		this.name = setting.getName();
		this.colorValue = colorValue;
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
		Raion.INSTANCE.getGui()
			.getTheme()
			.subSettingButtonDraw(this, settingButton.button.categoryPanel.x + 1, settingButton.button.categoryPanel.y - settingButton.button.categoryPanel.scrollY + settingButton.button.y + settingButton.y + y, mouseX, mouseY);
	}
	
	public void mouseClicked(float mouseX, float mouseY, int state) {
		Raion.INSTANCE.getGui()
			.getTheme()
			.subSettingButtonMouseClicked(this, settingButton.button.categoryPanel.x + 1, settingButton.button.categoryPanel.y - settingButton.button.categoryPanel.scrollY + settingButton.button.y + settingButton.y + y, mouseX, mouseY, state);
	}
	
	public void mouseReleased(float mouseX, float mouseY, int state) {
		Raion.INSTANCE.getGui().getTheme().subSettingButtonMouseReleased(this, mouseX, mouseY, state);
	}
	
}
