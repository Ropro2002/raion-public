package me.robeart.raion.client.gui.hud;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.font.Fonts;
import me.robeart.raion.client.util.font.MinecraftFontRenderer;
import me.robeart.raion.client.util.minecraft.GLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class TogglePopup implements Popup {
	
	private final MinecraftFontRenderer font = Fonts.INSTANCE.getFont36();
	public int ycount;
	public int time;
	public boolean destroy;
	private Module module;
	private String state;
	private int xoffset;
	
	public TogglePopup(Module module, boolean state) {
		this.module = module;
		this.state = state ? "enabled" : "disabled";
		this.ycount = (new ScaledResolution(Minecraft.getMinecraft())).getScaledHeight() / 4;
		this.xoffset = 0;
		this.destroy = false;
		this.time = Minecraft.getDebugFPS() * 2;
	}
	
	@Override
	public void render() {
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		int x = res.getScaledWidth() - xoffset;
		float finalx = 13 + font.getStringWidth(state) + font.getStringWidth(module.getName());
		if (time <= 0) {
			if (xoffset < 0) destroy = true;
			else xoffset -= 5;
		}
		else {
			if (xoffset >= finalx) xoffset = (int) finalx;
			else xoffset += 5;
		}
		GLUtils.drawRect(x, res.getScaledHeight() - ycount, 13 + font.getStringWidth(state) + font.getStringWidth(module
			.getName()), 15, Integer.MIN_VALUE);
		font.drawString(module.getName(), x + 5, res.getScaledHeight() - ycount + (15 - font.getStringHeight(module.getName())) / 2, 0xFFFFFFFF);
		font.drawString(state, x + font.getStringWidth(module.getName()) + 8, res.getScaledHeight() - ycount + (15 - font
			.getStringHeight(module.getName())) / 2, state == "enabled" ? 0xFF00FF00 : 0xFFFF0000);
		
	}
	
	@Override
	public int getY() {
		return ycount;
	}
	
	@Override
	public void setY(int y) {
		this.ycount = y;
	}
	
	@Override
	public int getTime() {
		return time;
	}
	
	@Override
	public void setTime(int time) {
		this.time = time;
	}
	
	@Override
	public boolean getDestroy() {
		return destroy;
	}
	
	@Override
	public void setDestroy(boolean destroy) {
		this.destroy = destroy;
	}
}
