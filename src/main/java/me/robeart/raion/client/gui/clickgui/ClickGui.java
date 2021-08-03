package me.robeart.raion.client.gui.clickgui;


import me.robeart.raion.client.gui.clickgui.theme.RaionTheme;
import me.robeart.raion.client.gui.clickgui.theme.Theme;
import me.robeart.raion.client.module.ClickGuiModule;
import me.robeart.raion.client.util.Utils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

public final class ClickGui extends GuiScreen {
	private GuiScreen parent;
	private MainPanel mainPanel;
	private Theme theme;
	
	public ClickGui() {
		this.theme = new RaionTheme();
		this.mainPanel = new MainPanel("RaionTheme", 50, 50, this.theme);
		this.theme.mainConstructor(this, this.mainPanel);
	}
	
	public void setParent(GuiScreen parent) {
		this.parent = parent;
	}
	
	public Theme getTheme() {
		return this.theme;
	}
	
	public MainPanel getMainPanel() {
		return this.mainPanel;
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	boolean shaderLoaded = false;
	private void loadShader() {
		//noinspection ConstantConditions
		if (OpenGlHelper.shadersSupported && mc.entityRenderer.getShaderGroup() == null) {
			mc.entityRenderer.loadShader(new ResourceLocation("minecraft", "shaders/post/blur.json"));
			shaderLoaded = true;
		}
	}
	
	@Override
	public void initGui() {
		if (mc.world != null) {
			String mode = ClickGuiModule.INSTANCE.getBackgroundStyle();
			
			if (mode.equals("Blur")) {
				loadShader();
			}
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.pushMatrix();
		float scale = 1f;//ClickGuiModule.INSTANCE.getScale();
		float sX = (float) mouseX * scale;
		float sY = (float) mouseY * scale;
		
		if (mc.world == null) {
			drawDefaultBackground(); // Dirt background
		} else {
			String mode = ClickGuiModule.INSTANCE.getBackgroundStyle();
			
			switch (mode) {
				case "Opaque":
					int opacity = (int)(ClickGuiModule.INSTANCE.getBackgroundOpacity() * 255);
					//int start = Utils.getRgb(63, 239, 239, opacity); // 240 a
					//int end = Utils.getRgb(63, 239, 239, opacity);
					//this.drawGradientRect(0, 0, this.width, this.height, start, end);
					
					Color start = new Color(-1072689136);
					Color end = new Color(-804253680);
					int startI = Utils.getRgb(start.getRed(), start.getGreen(), start.getBlue(), opacity);
					int endI = Utils.getRgb(end.getRed(), end.getGreen(), end.getBlue(), opacity);
					
					this.drawGradientRect(0, 0, this.width, this.height, startI, endI);
					break;
				case "Blur":
					if (!shaderLoaded) {
						loadShader();
					}
					break;
			}
			
			if (!mode.equals("Blur") && shaderLoaded) {
				mc.entityRenderer.stopUseShader();
				shaderLoaded = false;
			}
		}
		
		super.drawScreen((int) sX, (int) sY, partialTicks);
		this.mainPanel.draw(sX, sY);
		theme.postRender(sX, sY);
		
		GlStateManager.popMatrix();
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		float scale = 1f;//ClickGuiModule.INSTANCE.getScale();
		float sX = (float) mouseX * scale;
		float sY = (float) mouseY * scale;
		this.mainPanel.mouseReleased(sX, sY, mouseButton);
		super.mouseReleased((int) sX, (int) sY, mouseButton);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		float scale = 1f;//ClickGuiModule.INSTANCE.getScale();
		float sX = (float) mouseX * scale;
		float sY = (float) mouseY * scale;
		this.mainPanel.mouseClicked(sX, sY, mouseButton);
		super.mouseClicked((int) sX, (int) sY, mouseButton);
	}
	
	@Override
	public void handleKeyboardInput() throws IOException {
		super.handleKeyboardInput();
		if (Keyboard.getEventKeyState()) {
			int key = Keyboard.getEventKey();
			if (key != Keyboard.KEY_NONE) {
				this.mainPanel.keyPressed(key);
			}
		}
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1) {
			mc.displayGuiScreen(parent);
			parent = null;
			
			if (mc.currentScreen == null) {
				mc.setIngameFocus();
			}
			return;
		}
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	public void onGuiClosed() {
		if (shaderLoaded) {
			mc.entityRenderer.stopUseShader();
			shaderLoaded = false;
		}
		
		this.theme.onClose(this.mainPanel);
	}
}
