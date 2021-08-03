package me.robeart.raion.client.module;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.events.events.client.ToggleEvent;
import me.robeart.raion.client.util.Interpolation;
import me.robeart.raion.client.util.font.MinecraftFontRenderer;
import me.robeart.raion.client.value.Value;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
	
	protected static final Minecraft mc = Minecraft.getMinecraft();
	
	private String name;
	private String[] alias;
	private String description;
	private Category category;
	private String bind;
	private boolean visible;
	
	private boolean state;
	private List<Value> values = new ArrayList<>();
	
	public Module(String name, String[] alias, String keybind, String description, Category category) {
		this.name = name;
		this.alias = alias;
		this.bind = keybind;
		this.description = description;
		this.category = category;
		this.visible = true;
	}
	
	public Module(String name, String keybind, String description, Category category) {
		this.name = name;
		this.bind = keybind;
		this.description = description;
		this.category = category;
		this.visible = true;
	}
	
	public Module(String name, String keybind, String description, Category category, boolean visible) {
		this.name = name;
		this.bind = keybind;
		this.description = description;
		this.category = category;
		this.visible = visible;
	}
	
	public Module(String name, String[] alias, String description, Category category) {
		this.name = name;
		this.alias = alias;
		this.bind = "NONE";
		this.description = description;
		this.category = category;
		this.visible = true;
	}
	
	public Module(String name, String description, Category category) {
		this.name = name;
		this.bind = "NONE";
		this.description = description;
		this.category = category;
		this.visible = true;
	}
	
	public Module(String name, String description, Category category, boolean visible) {
		this.name = name;
		this.bind = "NONE";
		this.description = description;
		this.category = category;
		this.visible = visible;
	}
	
	
	public final void toggle() {
		setState(!getState());
	}
	
	public void onEnable() {
	}
	
	public void onDisable() {
	}
	
	public void moduleLogic() {
	}
	
	public String getHudInfo() {
		return null;
	}
	
	public boolean getVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String[] getAlias() {
		return this.alias;
	}
	
	public void setAlias(String[] alias) {
		this.alias = alias;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Category getCategory() {
		return category;
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public boolean getState() {
		return state;
	}
	
	public void setState(boolean state) {
		this.state = state;
		try {
			if (state) {
				Raion.INSTANCE.getEventManager().addEventListener(this);
				Raion.INSTANCE.getEventManager().dispatchEvent(new ToggleEvent(this));
				onEnable();
				
			}
			else {
				Raion.INSTANCE.getEventManager().removeEventListener(this);
				Raion.INSTANCE.getEventManager().dispatchEvent(new ToggleEvent(this));
				onDisable();
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public float nameWidth = 0;
	public float totalWidth = 0;
	public float desiredWidth = 0;
	public float currentWidth = -1;
	public void recalculateWidth(MinecraftFontRenderer font, float interpSpeed) {
		String info = this.getHudInfo();
		if (info == null) info = "";
		else info = " " + info;
		nameWidth = font.getStringWidth(name);
		totalWidth = nameWidth + font.getStringWidth(info);
		
		desiredWidth = state ? totalWidth : 0;
		if(currentWidth == desiredWidth) return;
		
		if (currentWidth == -1) {
			currentWidth = desiredWidth;
		} else {
			currentWidth = Interpolation.finterpTo(currentWidth, desiredWidth, mc.getRenderPartialTicks(), interpSpeed);
		}
	}
	private float currentHeight = -1;
	public float calculateCurrentHeight(float desired, float interpSpeed) {
		if (currentHeight == -1) {
			currentHeight = desired;
			return desired;
		}
		currentHeight = Interpolation.finterpTo(currentHeight, desired, mc.getRenderPartialTicks(), interpSpeed);
		return currentHeight;
	}
	
	public String getBind() {
		return bind;
	}
	
	public void setBind(String bind) {
		this.bind = bind;
	}
	
	public List<Value> getValues() {
		return values;
	}
	
	public enum Category {
		EXPLOIT("Exploit", false),
		MOVEMENT("Movement", false),
		RENDER("Render", false),
		COMBAT("Combat", false),
		PLAYER("Player", false),
		MISC("Misc", false),
		HIDDEN("Hidden", true);
		
		boolean hidden;
		String name;
		
		Category(String name, boolean hidden) {
			this.name = name;
			this.hidden = hidden;
		}
		
		public String getName() {
			return name;
		}
	}
	
}
