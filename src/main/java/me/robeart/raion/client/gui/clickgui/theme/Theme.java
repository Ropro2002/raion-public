package me.robeart.raion.client.gui.clickgui.theme;

import me.robeart.raion.client.gui.clickgui.*;
import net.minecraft.util.ResourceLocation;

public interface Theme {
	
	void mainConstructor(ClickGui clickGui, MainPanel mainPanel);
	
	void onClose(MainPanel mainPanel);
	
	void mainPanelDraw(MainPanel mainPanel, float mouseX, float mouseY);
	
	boolean mainPanelKeyPress(MainPanel mainPanel, int key);
	
	void mainPanelConstructor(MainPanel mainPanel, float x, float y);
	
	void mainPanelMouseClicked(MainPanel mainPanel, float mouseX, float mouseY, int state);
	
	void mainPanelMouseReleased(MainPanel mainPanel, float mouseX, float mouseY, int state);
	
	void categoryButtonConstructor(CategoryButton categoryButton, MainPanel mainPanel);
	
	void categoryButtonMouseClicked(CategoryButton categoryButton, MainPanel mainPanel, float mouseX, float mouseY, int state);
	
	void categoryButtonDraw(CategoryButton categoryButton, float mouseX, float mouseY, ResourceLocation image);
	
	boolean categoryButtonKeyPress(CategoryButton categoryButton, int key);
	
	void categoryPanelConstructor(CategoryPanel categoryPanel, CategoryButton categoryButton, float x, float y);
	
	void categoryPanelMouseClicked(CategoryPanel categoryPanel, float mouseX, float mouseY, int state);
	
	void categoryPanelMouseReleased(CategoryPanel categoryPanel, float mouseX, float mouseY, int state);
	
	void categoryPanelDraw(CategoryPanel categoryPanel, float mouseX, float mouseY);
	
	void buttonContructor(Button button, CategoryPanel categoryPanel);
	
	void buttonMouseClicked(Button button, float mouseX, float mouseY, int state, CategoryPanel categoryPanel);
	
	void buttonMouseReleased(Button button, float mouseX, float mouseY, int state, CategoryPanel categoryPanel);
	
	void buttonDraw(Button button, float x, float y, float mouseX, float mouseY, CategoryPanel categoryPanel);
	
	void settingButtonConstructor(SettingButton settingButton, Button button);
	
	void settingButtonDraw(SettingButton settingButton, float x, float y, float mouseX, float mouseY);
	
	void settingButtonMouseClicked(SettingButton settingButton, float x, float y, float mouseX, float mouseY, int state);
	
	void settingButtonMouseReleased(SettingButton settingButton, float mouseX, float mouseY, int state);
	
	void subSettingButtonDraw(SubSettingButton SubSettingButton, float x, float y, float mouseX, float mouseY);
	
	void subSettingButtonMouseClicked(SubSettingButton subSettingButton, float x, float y, float mouseX, float mouseY, int state);
	
	void subSettingButtonMouseReleased(SubSettingButton subSettingButton, float mouseX, float mouseY, int state);
	
	void bindButtonMouseClicked(BindButton bindButton, float mouseX, float mouseY, int state, Button button);
	
	void bindButtonDraw(BindButton bindButton, float mouseX, float mouseY, Button button);
	
	void postRender(float mouseX, float mouseY);
	
}
