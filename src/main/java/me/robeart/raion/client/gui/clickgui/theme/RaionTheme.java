package me.robeart.raion.client.gui.clickgui.theme;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.gui.clickgui.Button;
import me.robeart.raion.client.gui.clickgui.*;
import me.robeart.raion.client.module.ClickGuiModule;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.module.render.HudModule;
import me.robeart.raion.client.util.Interpolation;
import me.robeart.raion.client.util.MathUtils;
import me.robeart.raion.client.util.font.Fonts;
import me.robeart.raion.client.util.font.MinecraftFontRenderer;
import me.robeart.raion.client.util.minecraft.GLUtils;
import me.robeart.raion.client.value.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static me.robeart.raion.client.util.Utils.getRgb;

public class RaionTheme extends GLUtils implements Theme {
	private static final Minecraft mc = Minecraft.getMinecraft();
	String currentFilter = "";
	private Font font = Raion.INSTANCE.getFont();
	private final MinecraftFontRenderer guiTitle = Fonts.INSTANCE.getFont80(),
		versionTitle = Fonts.INSTANCE.getFont40(),
		guiHeader = Fonts.INSTANCE.getFont48(),
		guiModule = Fonts.INSTANCE.getFont42(),
		guiSetting = Fonts.INSTANCE.getFont36(),
		guiDescription = Fonts.INSTANCE.getFont28();
	private ResourceLocation exploit = new ResourceLocation("textures/gui/clickgui/exploit.png"),
		movement = new ResourceLocation("textures/gui/clickgui/movement.png"),
		combat = new ResourceLocation("textures/gui/clickgui/combat.png"),
		render = new ResourceLocation("textures/gui/clickgui/render.png"),
		player = new ResourceLocation("textures/gui/clickgui/player.png"),
		misc = new ResourceLocation("textures/gui/clickgui/misc.png");
	public String tooltipDesc;
	private int scrollDy;
	private HudModule hudModule = null;
	
	@Override
	public void mainConstructor(ClickGui clickGui, MainPanel mainPanel) {
		mainPanel.typeButtons.add(new CategoryButton(Module.Category.EXPLOIT, -135, 5, exploit, mainPanel));
		mainPanel.typeButtons.add(new CategoryButton(Module.Category.MOVEMENT, -85, 5, movement, mainPanel));
		mainPanel.typeButtons.add(new CategoryButton(Module.Category.COMBAT, -35, 5, combat, mainPanel));
		mainPanel.typeButtons.add(new CategoryButton(Module.Category.RENDER, 15, 5, render, mainPanel));
		mainPanel.typeButtons.add(new CategoryButton(Module.Category.PLAYER, 65, 5, player, mainPanel));
		mainPanel.typeButtons.add(new CategoryButton(Module.Category.MISC, 115, 5, misc, mainPanel));
	}
	
	@Override
	public void onClose(MainPanel mainPanel) {
		Raion.INSTANCE.getConfigManager().saveAll();
	}
	
	@Override
	public void mainPanelDraw(MainPanel mainPanel, float mouseX, float mouseY) {
		scrollDy = Mouse.getDWheel();
		drawRect(0, 0, getScreenWidth(), 30, getRgb(0, 0, 0, 50));
		drawRect(0, 0, getScreenWidth(), 1, getColor(100));
		drawRect(getScreenWidth() / 2f - 150, 3, 1, 24, getColor(100));
		drawRect(getScreenWidth() / 2f + 150, 3, 1, 24, getColor(100));
		
		String leftTitle = "Raion " + Raion.VERSION;
		if (getHudModule().weeb.getValue()) {
			leftTitle = "\u30e9\u30a4\u30aa\u30f3";
		}
		guiTitle.drawString(leftTitle, 5, (30 - guiTitle.getStringHeight(leftTitle)) / 2, 0xFFFFFFFF);
		
		String removeVersionTitle = Raion.VERSION_INFO;
		String localVersionTitle = Raion.LOCAL_VERSION_INFO;
		float localHeight = 30 - versionTitle.getStringHeight(localVersionTitle);
		float remoteHeight = localHeight - versionTitle.getStringHeight(removeVersionTitle);
		
		versionTitle.drawString(localVersionTitle, getScreenWidth() - versionTitle.getStringWidth(localVersionTitle), localHeight, getRgb(171, 171, 171, 200));
		versionTitle.drawString(removeVersionTitle, getScreenWidth() - versionTitle.getStringWidth(removeVersionTitle), remoteHeight, getRgb(171, 171, 171, 200));
		
		for (CategoryButton button : mainPanel.typeButtons) button.draw(mouseX, mouseY);
	}
	
	@Override
	public boolean mainPanelKeyPress(MainPanel mainPanel, int key) {
		for (CategoryButton categoryButton : mainPanel.typeButtons) {
			if (categoryButton.enabled) {
				if (categoryButton.keyPressed(key)) {
					return true;
				}
			}
		}
		if (key == Keyboard.KEY_BACK) {
			if (currentFilter.length() >= 1) {
				currentFilter = currentFilter.substring(0, currentFilter.length() - 1);
			}
		}
		else if (key == Keyboard.KEY_DELETE) {
			currentFilter = "";
		}
		else if (key == Keyboard.KEY_RETURN) {
			currentFilter = "";
		}
		else {
			char keyChar = Keyboard.getEventCharacter();
			if (Character.isAlphabetic(keyChar)) {
				currentFilter += keyChar;
			}
		}
		return true;
	}
	
	@Override
	public void mainPanelConstructor(MainPanel mainPanel, float x, float y) {
	
	}
	
	@Override
	public void mainPanelMouseClicked(MainPanel mainPanel, float mouseX, float mouseY, int state) {
		for (CategoryButton button : mainPanel.typeButtons) button.mouseClicked(mouseX, mouseY, state);
	}
	
	@Override
	public void mainPanelMouseReleased(MainPanel mainPanel, float mouseX, float mouseY, int state) {
		for (CategoryButton categoryButton : mainPanel.typeButtons) {
			if (categoryButton.enabled) categoryButton.categoryPanel.mouseReleased(mouseX, mouseY, state);
		}
	}
	
	@Override
	public void categoryButtonConstructor(CategoryButton categoryButton, MainPanel mainPanel) {
		int x = getScreenWidth() / 2;
		categoryButton.categoryPanel = new CategoryPanel(categoryButton.category.getName(), categoryButton, x + categoryButton.x - 40, 40);
	}
	
	@Override
	public void categoryButtonMouseClicked(CategoryButton categoryButton, MainPanel mainPanel, float mouseX, float mouseY, int state) {
		int x = getScreenWidth() / 2;
		boolean hovering = isHovered(x + categoryButton.x, categoryButton.y, 20, 20, mouseX, mouseY);
		if (hovering && state == 0) {
			categoryButton.enabled = !categoryButton.enabled;
		}
		else if (categoryButton.enabled) categoryButton.categoryPanel.mouseClicked(mouseX, mouseY, state);
	}
	
	@Override
	public void categoryButtonDraw(CategoryButton categoryButton, float mouseX, float mouseY, ResourceLocation image) {
		int x = getScreenWidth() / 2;
		boolean hovering = isHovered(x + categoryButton.x, categoryButton.y, 20, 20, mouseX, mouseY);
		Minecraft.getMinecraft().getTextureManager().bindTexture(image);
		glColor(categoryButton.getColour());
		GLUtils.drawCompleteImage(x + categoryButton.x, categoryButton.y, 20, 20);
		if (categoryButton.enabled) categoryButton.categoryPanel.draw(mouseX, mouseY);
		else if (hovering) {
			categoryButton.categoryPanel.x = x + categoryButton.x - 40;
			categoryButton.categoryPanel.y = 40;
			categoryButton.categoryPanel.draw(mouseX, mouseY);
		}
		
	}
	
	@Override
	public boolean categoryButtonKeyPress(CategoryButton categoryButton, int key) {
		for (Button button : categoryButton.categoryPanel.buttons) {
			if (button.binding) {
				if (Keyboard.KEY_TAB == key || Keyboard.KEY_ESCAPE == key || Keyboard.KEY_DELETE == key)
					button.module.setBind("NONE");
				else button.module.setBind(Keyboard.getKeyName(key));
				button.binding = false;
				return true;
			}
		}
		return false;
	}
	
	private List<Module> getModules(Module.Category category) {
		List<Module> modules = new ArrayList<>();
		for (Module module : Raion.INSTANCE.getModuleManager().getModuleList()) {
			if (module.getCategory() == category) {
				modules.add(module);
			}
		}
		return modules.isEmpty() ? null : modules;
	}
	
	private List<Value> getSettings(Module module) {
		return module.getValues().isEmpty() ? null : module.getValues();
	}
	
	@Override
	public void categoryPanelConstructor(CategoryPanel categoryPanel, CategoryButton categoryButton, float x, float y) {
		try {
			for (Module module : this.getModules(categoryButton.category)) {
				categoryPanel.buttonsActual.add(new Button(categoryPanel, module.getName(), module.getDescription(), 5, 16 + categoryPanel.height, module));
				categoryPanel.height += 14;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void categoryPanelMouseClicked(CategoryPanel categoryPanel, float mouseX, float mouseY, int state) {
		boolean hovering = isHovered(categoryPanel.x - 2, categoryPanel.y - categoryPanel.scrollY, categoryPanel.width + 4, 16, mouseX, mouseY);
		if (hovering && state == 0) {
			categoryPanel.dragx = mouseX;
			categoryPanel.dragy = mouseY;
			categoryPanel.dragging = true;
		} else if (hovering && state == 1) {
			categoryPanel.enabled = !categoryPanel.enabled;
		} else {
			for (Button button : categoryPanel.buttons) {
				button.mouseClicked(mouseX, mouseY, state);
			}
		}
	}
	
	@Override
	public void categoryPanelMouseReleased(CategoryPanel categoryPanel, float mouseX, float mouseY, int state) {
		if (categoryPanel.dragging) {
			categoryPanel.dragging = false;
		} else {
			for (Button button : categoryPanel.buttons) {
				button.mouseReleased(mouseX, mouseY, state);
			}
		}
	}
	
	@Override
	public void categoryPanelDraw(CategoryPanel categoryPanel, float mouseX, float mouseY) {
		float target = Math.min(ClickGuiModule.INSTANCE.getMaxCategorySize(), categoryPanel.enabled ? categoryPanel.height : 0);
		if (categoryPanel.renderedHeight == -1) {
			categoryPanel.renderedHeight = target;
		} else {
			categoryPanel.renderedHeight = Interpolation.finterpTo(categoryPanel.renderedHeight, target, mc.getRenderPartialTicks(), ClickGuiModule.INSTANCE.getColourSpeed());
		}
		categoryPanel.listY = categoryPanel.y + 16;
		float scrollDY = 0;
		if (ClickGuiModule.INSTANCE.getUseEventScroll()) {
			scrollDY = MathHelper.clamp(Mouse.getEventDWheel(), -1, 1) * ClickGuiModule.INSTANCE.getScrollSpeed();
		} else {
			scrollDY = MathHelper.clamp(this.scrollDy, -1, 1) * ClickGuiModule.INSTANCE.getScrollSpeed();
		}
		if (mouseX >= categoryPanel.x && mouseX <= (categoryPanel.x + categoryPanel.width) && mouseY >= categoryPanel.y && mouseY <= (categoryPanel.y + categoryPanel.renderedHeight)) {
			categoryPanel.scrollY -= scrollDY;
		}
		target = MathHelper.clamp(categoryPanel.scrollY, 0, (categoryPanel.height - categoryPanel.renderedHeight));
		categoryPanel.scrollY = Interpolation.finterpTo(categoryPanel.scrollY, target, mc.getRenderPartialTicks(), ClickGuiModule.INSTANCE.getColourSpeed());
		
		ScaledResolution resolution = new ScaledResolution(mc);
		double scaleWidth = (double) mc.displayWidth / resolution.getScaledWidth_double();
		double scaleHeight = (double) mc.displayHeight / resolution.getScaledHeight_double();
		
		int extra = categoryPanel.renderedHeight > 0 ? 19 : 18;
		drawRect(categoryPanel.x - 2, categoryPanel.y - 2, categoryPanel.width + 4, extra + categoryPanel.renderedHeight, getRgb(0, 0, 0, 70));
		drawRect(categoryPanel.x, categoryPanel.y, categoryPanel.width, 14, getRgb(0, 0, 0, 100));
		guiHeader.drawCenteredString(categoryPanel.headerString, categoryPanel.x + (categoryPanel.width / 2), categoryPanel.y + 2, -1);
		
		if (categoryPanel.renderedHeight > 0) {
			GL11.glPushMatrix();
			GL11.glScissor(
				(int) (categoryPanel.x * scaleWidth),
				(mc.displayHeight) - (int) ((categoryPanel.y + 16 + categoryPanel.renderedHeight) * scaleHeight),
				(int) ((categoryPanel.width + 4) * scaleWidth),
				(int) (categoryPanel.renderedHeight * scaleHeight)
			);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			//drawRect(categoryPanel.x + categoryPanel.width, categoryPanel.y + 16, 2, categoryPanel.height, getRgb(0, 0, 0, 50));
			//System.out.println(Arrays.toString(new int[]{categoryPanel.x, categoryPanel.y, categoryPanel.width, renderedHeight}));
			for (Button button : categoryPanel.buttons) button.draw(mouseX, mouseY);
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			GL11.glPopMatrix();
		}
		if (categoryPanel.dragging) {
			categoryPanel.x += mouseX - categoryPanel.dragx;
			categoryPanel.y += mouseY - categoryPanel.dragy;
			categoryPanel.dragx = mouseX;
			categoryPanel.dragy = mouseY;
		}
	}
	
	@Override
	public void buttonContructor(Button button, CategoryPanel categoryPanel) {
		button.settings = getSettings(button.module);
		if (button.settings != null) {
			for (Value value : button.settings) {
				if (value.getParentSetting() != null) {
					button.subSettings.add(value);
				}
			}
			for (Value value : button.settings) {
				if (value.getParentSetting() == null) {
					if (value instanceof ColorValue) {
						ColorValue value1 = (ColorValue) value;
						for (IntValue v : value1.getColors()) {
							button.settingButtons.add(new SettingButton(button, button.settingsY, v, value1));
							button.settingsY += 12;
						}
						continue;
					}
					button.settingButtons.add(new SettingButton(button, button.settingsY, value));
					button.settingsY += 12;
				}
			}
		}
		button.bindButton = new BindButton(button, categoryPanel.x, button.settingsY, button.module);
		button.settingsY -= 2;
	}
	
	@Override
	public void buttonMouseClicked(Button button, float mouseX, float mouseY, int state, CategoryPanel categoryPanel) {
		boolean hovering = mouseY < categoryPanel.listY + categoryPanel.renderedHeight && isHovered(categoryPanel.x, button.categoryPanel.y - categoryPanel.scrollY + button.y, categoryPanel.width, 13, mouseX, mouseY);
		if (hovering) {
			if (state == 0) button.module.setState(!button.module.getState());
			if (state == 1) {
				for (Button button1 : categoryPanel.buttons) {
					if (button1.y > button.y && button1 != button) {
						button1.y += button.settingsopen ? -button.settingsY : button.settingsY;
					}
				}
				button.categoryPanel.height += button.settingsopen ? -button.settingsY : button.settingsY;
				button.settingsopen = !button.settingsopen;
			}
		}
		else {
			button.bindButton.mouseClicked(mouseX, mouseY, state);
			for (SettingButton settingButton : button.settingButtons)
				settingButton.mouseClicked(mouseX, mouseY, state);
		}
	}
	
	@Override
	public void buttonMouseReleased(Button button, float mouseX, float mouseY, int state, CategoryPanel categoryPanel) {
		for (SettingButton settingButton : button.settingButtons) settingButton.mouseReleased(mouseX, mouseY, state);
	}
	
	private HudModule getHudModule() {
		if (hudModule == null) {
			hudModule = (HudModule) Raion.INSTANCE.getModuleManager().getModule(HudModule.class);
		}
		return hudModule;
	}
	
	@Override
	public void buttonDraw(Button button, float x, float y, float mouseX, float mouseY, CategoryPanel categoryPanel) {
		boolean hovering = mouseY < categoryPanel.listY + categoryPanel.renderedHeight && isHovered(x, y, categoryPanel.width, 13, mouseX, mouseY);
		
		if (hovering) {
			tooltipDesc = button.description;
		}
		
		drawRect(x, y, categoryPanel.width, 13, button.getColour(hovering));
		//drawRect(x, y + button.y , categoryPanel.width, 1, getColor(60));
		int alpha = button.getAlphaOverlay();
		if (alpha != 0) {
			drawRect(x, y, categoryPanel.width, 13, getColor(alpha));
		}
		//if (button.module.getState()) {
		//}
		
		guiModule.drawString(button.name, x + 3, y + ((13 - guiModule.getStringHeight(button.name)) / 2), 0xCCFFFFFF);
		
		if (button.settingsopen) {
			//TODO Show description
			for (SettingButton settingButton : button.settingButtons) settingButton.draw(mouseX, mouseY);
			button.bindButton.draw(mouseX, mouseY);
		}
	}
	
	@Override
	public void postRender(float mouseX, float mouseY) {
		if (tooltipDesc != null) {
			if (getHudModule().tooltips.getValue()) {
				// render description tooltip
				float tooltipX = mouseX + 4;
				float tooltipY = mouseY + 4;
				float height = 13;
				float width = guiModule.getStringWidth(tooltipDesc) + 3;
				
				drawRect(tooltipX, tooltipY, width, height, getRgb(0, 0, 0, 220));
				guiModule.drawString(tooltipDesc, tooltipX + 2, tooltipY + ((13 - guiModule.getStringHeight(tooltipDesc)) / 2), 0xCCFFFFFF);
			}
			tooltipDesc = null;
		}
	}
	
	@Override
	public void settingButtonConstructor(SettingButton settingButton, Button button) {
		if (settingButton.subSettings != null) {
			for (Value v : settingButton.button.settings) {
				if (v.getParentSetting() == settingButton.setting) {
					settingButton.subSettings.add(v);
				}
			}
			if (settingButton.setting instanceof ListValue) {
				List<String> values = ((ListValue) settingButton.setting).getOptions();
				for (String s : values) {
					List<SubSettingButton> subSettingButtons = new ArrayList<>();
					int subsettingsY = 0;
					for (Value value : settingButton.subSettings) {
						if (s.equalsIgnoreCase(value.getListFilter())) {
							subsettingsY += 12;
							subSettingButtons.add(new SubSettingButton(settingButton, subsettingsY, value));
						}
					}
					settingButton.listSubSettings.add(subSettingButtons);
					settingButton.listSubSettingsY.add(subsettingsY);
				}
			}
			else {
				for (Value value : settingButton.subSettings) {
					if (value instanceof ColorValue) {
						ColorValue value1 = (ColorValue) value;
						for (IntValue v : value1.getColors()) {
							settingButton.settingsY += 12;
							settingButton.subSettingButtons.add(new SubSettingButton(settingButton, settingButton.settingsY, v, value1));
						}
						continue;
					}
					settingButton.settingsY += 12;
					settingButton.subSettingButtons.add(new SubSettingButton(settingButton, settingButton.settingsY, value));
				}
			}
		}
	}
	
	@Override
	public void settingButtonDraw(SettingButton settingButton, float x, float y, float mouseX, float mouseY) {
		CategoryPanel categoryPanel = settingButton.button.categoryPanel;
		boolean hovering = mouseY < categoryPanel.listY + categoryPanel.renderedHeight && isHovered(x, y, settingButton.button.categoryPanel.width, 11, mouseX, mouseY);
		
		if (hovering) {
			tooltipDesc = settingButton.setting.getDescription();
		}
		
		float rightMinus = !settingButton.subSettings.isEmpty() ? 2 : 1;
		drawRect(x + 1, y, settingButton.button.categoryPanel.width - rightMinus, 11, settingButton.getColour(hovering));
		drawRect(x, y - 1, 1, 12, getColor(60));
		if (!settingButton.subSettings.isEmpty())
			drawRect(x + settingButton.button.categoryPanel.width - 1, y, 1, 11, getColor(60));
		if (settingButton.setting instanceof BooleanValue) {
			BooleanValue booleanValue = (BooleanValue) settingButton.setting;
			drawRect(x + 1, y, settingButton.button.categoryPanel.width - rightMinus, 11, getColor(booleanValue.getAlphaOverlay()));
		}
		if (settingButton.setting instanceof ListValue) {
			guiSetting.drawString(((ListValue) settingButton.setting).getValue(), x + settingButton.button.categoryPanel.width - 3 - guiSetting
				.getStringWidth(((ListValue) settingButton.setting).getValue()), y + ((11 - guiSetting.getStringHeight(((ListValue) settingButton.setting)
				.getValue())) / 2), getRgb(167, 167, 178, 255));
		}
		if (settingButton.setting instanceof IntValue || settingButton.setting instanceof DoubleValue || settingButton.setting instanceof FloatValue) {
			float width = 0f;
			String value = "Error";
			if (settingButton.setting instanceof IntValue) {
				width = (int) ((settingButton.button.categoryPanel.width / 100f) * ((IntValue) settingButton.setting)
					.getPercentage());
				value = Integer.toString(((IntValue) settingButton.setting).getValue());
			}
			if (settingButton.setting instanceof DoubleValue) {
				width = (int) ((settingButton.button.categoryPanel.width / 100f) * ((DoubleValue) settingButton.setting)
					.getPercentage());
				value = Double.toString(MathUtils.round(((DoubleValue) settingButton.setting).getValue(), 2));
			}
			if (settingButton.setting instanceof FloatValue) {
				width = (int) ((settingButton.button.categoryPanel.width / 100f) * ((FloatValue) settingButton.setting)
					.getPercentage());
				value = Float.toString(MathUtils.round(((FloatValue) settingButton.setting).getValue(), 3));
			}
			drawRect(x, y, width, 11, getColor(40));
			guiSetting.drawString(value, x + settingButton.button.categoryPanel.width - 3 - guiSetting.getStringWidth(value), y + ((11 - guiSetting
				.getStringHeight(value)) / 2), getRgb(167, 167, 178, 255));
			if (settingButton.mouseDown) {
				float percentage = mouseX < x ? 0 : mouseX > x + settingButton.button.categoryPanel.width - 2 ? 100 : ((mouseX - x) / settingButton.button.categoryPanel.width * 100f);
				if (settingButton.setting instanceof IntValue) {
					((IntValue) settingButton.setting).setPercentage(percentage);
					if (settingButton.colorValue != null) {
						settingButton.colorValue.setColor();
					}
				}
				if (settingButton.setting instanceof DoubleValue)
					((DoubleValue) settingButton.setting).setPercentage(percentage);
				if (settingButton.setting instanceof FloatValue)
					((FloatValue) settingButton.setting).setPercentage(percentage);
			}
		}
		if (settingButton.settingsopen) {
			if (settingButton.setting instanceof ListValue) {
				for (List<SubSettingButton> l : settingButton.listSubSettings) {
					if (l.size() == 0) continue;
					if (l.get(0).setting.getListFilter()
						.equalsIgnoreCase(((ListValue) settingButton.setting).getValue())) {
						for (SubSettingButton s : l) s.draw(mouseX, mouseY);
						break;
					}
				}
			}
			else {
				for (SubSettingButton s : settingButton.subSettingButtons) s.draw(mouseX, mouseY);
			}
		}
		guiSetting.drawString(settingButton.name, x + 3, y + ((11 - guiSetting.getStringHeight(settingButton.name)) / 2), 0xCCFFFFFF);
	}
	
	@Override
	public void settingButtonMouseClicked(SettingButton settingButton, float x, float y, float mouseX, float mouseY, int state) {
		CategoryPanel categoryPanel = settingButton.button.categoryPanel;
		boolean hovering = mouseY < categoryPanel.listY + categoryPanel.renderedHeight && isHovered(x, y, settingButton.button.categoryPanel.width, 11, mouseX, mouseY);
		if (hovering && settingButton.button.settingsopen) {
			if (state == 0) {
				if (settingButton.setting instanceof BooleanValue)
					settingButton.setting.setValue(!((BooleanValue) settingButton.setting).getValue());
				if (settingButton.setting instanceof ListValue) {
					if (settingButton.settingsopen) {
						int index = 0;
						for (List<SubSettingButton> l : settingButton.listSubSettings) {
							if (l.size() == 0) continue;
							if (l.get(0).setting.getListFilter()
								.equalsIgnoreCase(((ListValue) settingButton.setting).getValue()))
								index = settingButton.listSubSettings.indexOf(l);
						}
						int current = settingButton.listSubSettingsY.get(index);
						((ListValue) settingButton.setting).nextValue();
						for (List<SubSettingButton> l : settingButton.listSubSettings) {
							if (l.size() == 0) continue;
							if (l.get(0).setting.getListFilter()
								.equalsIgnoreCase(((ListValue) settingButton.setting).getValue()))
								index = settingButton.listSubSettings.indexOf(l);
						}
						int next = settingButton.listSubSettingsY.get(index);
						int yoffset = next - current;
						for (Button button : settingButton.button.categoryPanel.buttons) {
							if (button.categoryPanel.y - button.categoryPanel.scrollY + button.y > y) {
								button.y += yoffset;
							}
						}
						for (SettingButton settingButton2 : settingButton.button.settingButtons) {
							if (settingButton2.button.categoryPanel.y - settingButton2.button.categoryPanel.scrollY + settingButton2.button.y + settingButton2.y > y) {
								settingButton2.y += yoffset;
							}
						}
						settingButton.button.settingsY += yoffset;
						settingButton.button.bindButton.y += yoffset;
						settingButton.button.categoryPanel.height += yoffset;
						
					}
					else {
						((ListValue) settingButton.setting).nextValue();
					}
				}
				settingButton.mouseDown = true;
			}
			if (state == 1) {
				if (!settingButton.subSettings.isEmpty()) {
					if (settingButton.setting instanceof ListValue) {
						int index = 0;
						for (List<SubSettingButton> l : settingButton.listSubSettings) {
							if (l.size() == 0) continue;
							if (l.get(0).setting.getListFilter()
								.equalsIgnoreCase(((ListValue) settingButton.setting).getValue()))
								index = settingButton.listSubSettings.indexOf(l);
						}
						settingButton.settingsY = settingButton.listSubSettingsY.get(index);
					}
					for (Button button : settingButton.button.categoryPanel.buttons) {
						if (button.categoryPanel.y - button.categoryPanel.scrollY + button.y > y) {
							button.y += settingButton.settingsopen ? -settingButton.settingsY : settingButton.settingsY;
						}
					}
					for (SettingButton settingButton2 : settingButton.button.settingButtons) {
						if (settingButton2.button.categoryPanel.y - settingButton2.button.categoryPanel.scrollY + settingButton2.button.y + settingButton2.y > y) {
							settingButton2.y += settingButton.settingsopen ? -settingButton.settingsY : settingButton.settingsY;
						}
					}
					settingButton.button.settingsY += settingButton.settingsopen ? -settingButton.settingsY : settingButton.settingsY;
					settingButton.button.bindButton.y += settingButton.settingsopen ? -settingButton.settingsY : settingButton.settingsY;
					settingButton.button.categoryPanel.height += settingButton.settingsopen ? -settingButton.settingsY : settingButton.settingsY;
					settingButton.settingsopen = !settingButton.settingsopen;
				}
			}
		}
		else {
			if (settingButton.settingsopen) {
				if (settingButton.setting instanceof ListValue) {
					int index = 0;
					for (List<SubSettingButton> l : settingButton.listSubSettings) {
						if (l.size() == 0) continue;
						if (l.get(0).setting.getListFilter()
							.equalsIgnoreCase(((ListValue) settingButton.setting).getValue()))
							index = settingButton.listSubSettings.indexOf(l);
					}
					for (SubSettingButton s : settingButton.listSubSettings.get(index))
						s.mouseClicked(mouseX, mouseY, state);
				}
				else {
					for (SubSettingButton s : settingButton.subSettingButtons) s.mouseClicked(mouseX, mouseY, state);
				}
			}
		}
	}
	
	@Override
	public void settingButtonMouseReleased(SettingButton settingButton, float mouseX, float mouseY, int state) {
		if (settingButton.mouseDown && state == 0) settingButton.mouseDown = false;
		else {
			if (settingButton.settingsopen) {
				
				if (settingButton.setting instanceof ListValue) {
					int index = 0;
					for (List<SubSettingButton> l : settingButton.listSubSettings) {
						if (l.size() == 0) continue;
						if (l.get(0).setting.getListFilter()
							.equalsIgnoreCase(((ListValue) settingButton.setting).getValue()))
							index = settingButton.listSubSettings.indexOf(l);
					}
					for (SubSettingButton s : settingButton.listSubSettings.get(index))
						s.mouseReleased(mouseX, mouseY, state);
				}
				else {
					for (SubSettingButton s : settingButton.subSettingButtons) s.mouseReleased(mouseX, mouseY, state);
				}
			}
		}
	}
	
	@Override
	public void subSettingButtonDraw(SubSettingButton subSettingButton, float x, float y, float mouseX, float mouseY) {
		CategoryPanel categoryPanel = subSettingButton.settingButton.button.categoryPanel;
		boolean hovering = mouseY < categoryPanel.listY + categoryPanel.renderedHeight && isHovered(x, y, subSettingButton.settingButton.button.categoryPanel.width - 1, 11, mouseX, mouseY);
		
		if (hovering) {
			tooltipDesc = subSettingButton.setting.getDescription();
		}
		
		drawRect(x + 1, y, subSettingButton.settingButton.button.categoryPanel.width - 2, 11, subSettingButton.getColour(hovering));
		drawRect(x, y - 1, 1, 12, getColor(60));
		drawRect(x - 1, y, 1, 11, getRgb(0, 0, 0, 50));
		if (subSettingButton.setting instanceof BooleanValue) {
			BooleanValue booleanValue = (BooleanValue) subSettingButton.setting;
			drawRect(x + 1, y, subSettingButton.settingButton.button.categoryPanel.width - 2, 11, getColor(booleanValue.getAlphaOverlay()));
		}
		if (subSettingButton.setting instanceof ListValue) {
			guiSetting.drawString(((ListValue) subSettingButton.setting).getValue(), x + subSettingButton.settingButton.button.categoryPanel.width - 3 - guiSetting
				.getStringWidth(((ListValue) subSettingButton.setting).getValue()), y + ((11 - guiSetting.getStringHeight(((ListValue) subSettingButton.setting)
				.getValue())) / 2), getRgb(167, 167, 178, 255));
		}
		if (subSettingButton.setting instanceof IntValue || subSettingButton.setting instanceof DoubleValue || subSettingButton.setting instanceof FloatValue) {
			int width = 0;
			String value = "Error";
			if (subSettingButton.setting instanceof IntValue) {
				width = (int) (((subSettingButton.settingButton.button.categoryPanel.width - 1) / 100f) * ((IntValue) subSettingButton.setting)
					.getPercentage());
				value = Integer.toString(((IntValue) subSettingButton.setting).getValue());
			}
			if (subSettingButton.setting instanceof DoubleValue) {
				width = (int) (((subSettingButton.settingButton.button.categoryPanel.width - 1) / 100f) * ((DoubleValue) subSettingButton.setting)
					.getPercentage());
				value = Double.toString(MathUtils.round(((DoubleValue) subSettingButton.setting).getValue(), 2));
			}
			if (subSettingButton.setting instanceof FloatValue) {
				width = (int) (((subSettingButton.settingButton.button.categoryPanel.width - 1) / 100f) * ((FloatValue) subSettingButton.setting)
					.getPercentage());
				value = Float.toString(MathUtils.round(((FloatValue) subSettingButton.setting).getValue(), 2));
			}
			drawRect(x, y, width, 11, getColor(40));
			guiSetting.drawString(value, x + subSettingButton.settingButton.button.categoryPanel.width - 3 - guiSetting.getStringWidth(value), y + ((11 - guiSetting
				.getStringHeight(value)) / 2), getRgb(167, 167, 178, 255));
			if (subSettingButton.mouseDown) {
				float percentage = mouseX < x ? 0 : mouseX > x + subSettingButton.settingButton.button.categoryPanel.width - 2 ? 100 : ((mouseX - x) / subSettingButton.settingButton.button.categoryPanel.width * 100f);
				if (subSettingButton.setting instanceof IntValue) {
					((IntValue) subSettingButton.setting).setPercentage(percentage);
					if (subSettingButton.colorValue != null) {
						subSettingButton.colorValue.setColor();
					}
				}
				if (subSettingButton.setting instanceof DoubleValue)
					((DoubleValue) subSettingButton.setting).setPercentage(percentage);
				if (subSettingButton.setting instanceof FloatValue)
					((FloatValue) subSettingButton.setting).setPercentage(percentage);
			}
		}
		guiSetting.drawString(subSettingButton.name, x + 4, y + ((11 - guiSetting.getStringHeight(subSettingButton.name)) / 2), 0xCCFFFFFF);
	}
	
	@Override
	public void subSettingButtonMouseClicked(SubSettingButton subSettingButton, float x, float y, float mouseX, float mouseY, int state) {
		CategoryPanel categoryPanel = subSettingButton.settingButton.button.categoryPanel;
		boolean hovering = mouseY < categoryPanel.listY + categoryPanel.renderedHeight && isHovered(x, y, subSettingButton.settingButton.button.categoryPanel.width - 1, 11, mouseX, mouseY);
		if (hovering && subSettingButton.settingButton.button.settingsopen && subSettingButton.settingButton.settingsopen) {
			if (state == 0) {
				if (subSettingButton.setting instanceof BooleanValue)
					subSettingButton.setting.setValue(!((BooleanValue) subSettingButton.setting).getValue());
				if (subSettingButton.setting instanceof ListValue) ((ListValue) subSettingButton.setting).nextValue();
				subSettingButton.mouseDown = true;
			}
		}
	}
	
	@Override
	public void subSettingButtonMouseReleased(SubSettingButton subSettingButton, float mouseX, float mouseY, int state) {
		if (subSettingButton.mouseDown && state == 0) subSettingButton.mouseDown = false;
	}
	
	@Override
	public void bindButtonDraw(BindButton bindButton, float mouseX, float mouseY, Button button) {
		CategoryPanel categoryPanel = bindButton.button.categoryPanel;
		boolean hovering = mouseY < categoryPanel.listY + categoryPanel.renderedHeight && isHovered(button.categoryPanel.x + 1, button.categoryPanel.y - button.categoryPanel.scrollY + button.y + bindButton.y, button.categoryPanel.width - 2, 11, mouseX, mouseY);
		drawRect(button.categoryPanel.x, button.categoryPanel.y - button.categoryPanel.scrollY + button.y + bindButton.y, button.categoryPanel.width, 11, bindButton.getColour(hovering));
		drawRect(button.categoryPanel.x, button.categoryPanel.y - button.categoryPanel.scrollY + button.y + bindButton.y - 1, 1, 12, getColor(60));
		guiSetting.drawString("Keybind", button.categoryPanel.x + 3, button.categoryPanel.y - button.categoryPanel.scrollY + button.y + bindButton.y + ((11 - guiSetting
			.getStringHeight("Keybind")) / 2), 0xCCFFFFFF);
		String key = button.binding ? "..." : bindButton.module.getBind();
		guiSetting.drawString(key, button.categoryPanel.x + button.categoryPanel.width - 3 - guiSetting.getStringWidth(key), button.categoryPanel.y - button.categoryPanel.scrollY + button.y + bindButton.y + ((11 - guiSetting
			.getStringHeight(key)) / 2), getRgb(167, 167, 178, 255));
		
	}
	
	@Override
	public void bindButtonMouseClicked(BindButton bindButton, float mouseX, float mouseY, int state, Button button) {
		CategoryPanel categoryPanel = bindButton.button.categoryPanel;
		boolean hovering = mouseY < categoryPanel.listY + categoryPanel.renderedHeight && isHovered(button.categoryPanel.x + 1, button.categoryPanel.y - button.categoryPanel.scrollY + button.y + bindButton.y, button.categoryPanel.width - 2, 11, mouseX, mouseY);
		if (hovering && state == 0 && button.settingsopen) {
			button.binding = !button.binding;
		}
	}
	
}
