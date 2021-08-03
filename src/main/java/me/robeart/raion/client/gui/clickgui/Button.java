package me.robeart.raion.client.gui.clickgui;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.module.ClickGuiModule;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.Interpolation;
import me.robeart.raion.client.value.Value;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

import static me.robeart.raion.client.util.Utils.getRgb;

public class Button {
	private static final Minecraft mc = Minecraft.getMinecraft();
	public float x;
	public float y;
	public String name;
	public String description;
	public CategoryPanel categoryPanel;
	public boolean enabled;
	public boolean binding;
	public boolean settingsopen;
	public float settingsY;
	public Module module;
	public BindButton bindButton;
	public List<Value> settings;
	public List<Value> subSettings;
	public List<SettingButton> settingButtons;
	
	public Button(CategoryPanel categoryPanel, String name, String description, float x, float y, Module module) {
		this.x = x;
		this.y = y;
		this.name = name;
		this.binding = false;
		this.settingsopen = false;
		this.settingsY = 14;
		this.description = description;
		this.categoryPanel = categoryPanel;
		this.module = module;
		this.enabled = module.getState();
		this.settingButtons = new ArrayList();
		this.subSettings = new ArrayList<>();
		categoryPanel.categoryButton.mainPanel.theme.buttonContructor(this, this.categoryPanel);
	}
	
	private int colour;
	public int getColour(boolean hovered) {
		int desired;
		if (hovered) {
			desired = getRgb(77, 77, 77, 100);
		} else {
			desired = getRgb(0, 0, 0, 80);
		}
		colour = Interpolation.cinterpTo(colour, desired, mc.getRenderPartialTicks(), ClickGuiModule.INSTANCE.getColourSpeed());
		return colour;
	}
	
	private float overlayAlpha;
	public int getAlphaOverlay() {
		float desired;
		if (module.getState()) {
			desired = 50;
		}
		else {
			desired = 0;
		}
		overlayAlpha = Interpolation.finterpTo(overlayAlpha, desired, mc.getRenderPartialTicks(), ClickGuiModule.INSTANCE.getColourSpeed());
		return (int) overlayAlpha;
	}
	
	public void draw(float mouseX, float mouseY) {
		Raion.INSTANCE.getGui()
			.getTheme()
			.buttonDraw(this, categoryPanel.x, categoryPanel.y - categoryPanel.scrollY + y, mouseX, mouseY, categoryPanel);
	}
	
	public void mouseClicked(float mouseX, float mouseY, int state) {
		Raion.INSTANCE.getGui().getTheme().buttonMouseClicked(this, mouseX, mouseY, state, this.categoryPanel);
	}
	
	public void mouseReleased(float mouseX, float mouseY, int state) {
		Raion.INSTANCE.getGui().getTheme().buttonMouseReleased(this, mouseX, mouseY, state, this.categoryPanel);
	}
}
