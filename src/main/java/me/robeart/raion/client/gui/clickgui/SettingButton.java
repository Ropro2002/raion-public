package me.robeart.raion.client.gui.clickgui;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.module.ClickGuiModule;
import me.robeart.raion.client.util.Interpolation;
import me.robeart.raion.client.value.ColorValue;
import me.robeart.raion.client.value.Value;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

import static me.robeart.raion.client.util.Utils.getRgb;

public class SettingButton {
	private static final Minecraft mc = Minecraft.getMinecraft();
	public Button button;
	public float y;
	public float settingsY;
	public boolean settingsopen;
	public boolean mouseDown;
	public String name;
	public Value setting;
	public List<Value> subSettings;
	public List<SubSettingButton> subSettingButtons;
	public List<List<SubSettingButton>> listSubSettings;
	public List<Integer> listSubSettingsY;
	public ColorValue colorValue;
	
	public SettingButton(Button button, float y, Value setting) {
		this.button = button;
		this.y = y;
		this.setting = setting;
		this.name = setting.getName();
		this.subSettings = new ArrayList<>();
		this.subSettingButtons = new ArrayList<>();
		this.listSubSettings = new ArrayList<>();
		this.listSubSettingsY = new ArrayList<>();
		this.colorValue = null;
		button.categoryPanel.categoryButton.mainPanel.theme.settingButtonConstructor(this, button);
	}
	
	public SettingButton(Button button, float y, Value setting, ColorValue colorValue) {
		this.button = button;
		this.y = y;
		this.setting = setting;
		this.name = setting.getName();
		this.subSettings = new ArrayList<>();
		this.subSettingButtons = new ArrayList<>();
		this.listSubSettings = new ArrayList<>();
		this.listSubSettingsY = new ArrayList<>();
		this.colorValue = colorValue;
		button.categoryPanel.categoryButton.mainPanel.theme.settingButtonConstructor(this, button);
	}
	
	private int colour;
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
			.settingButtonDraw(this, button.categoryPanel.x, button.categoryPanel.y - button.categoryPanel.scrollY + button.y + y, mouseX, mouseY);
	}
	
	public void mouseClicked(float mouseX, float mouseY, int state) {
		Raion.INSTANCE.getGui()
			.getTheme()
			.settingButtonMouseClicked(this, button.categoryPanel.x, button.categoryPanel.y - button.categoryPanel.scrollY + button.y + y, mouseX, mouseY, state);
	}
	
	public void mouseReleased(float mouseX, float mouseY, int state) {
		Raion.INSTANCE.getGui().getTheme().settingButtonMouseReleased(this, mouseX, mouseY, state);
	}
	
}
