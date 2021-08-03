package me.robeart.raion.client.gui.clickgui;

import me.robeart.raion.client.gui.clickgui.theme.Theme;

import java.util.ArrayList;

public class MainPanel {
	public int x;
	public int y;
	public String headerString;
	public CategoryPanel currentPanel = null;
	public ArrayList<CategoryButton> typeButtons;
	public ArrayList<CategoryPanel> typePanels;
	Theme theme;
	
	public MainPanel(String header, int x, int y, Theme theme) {
		this.headerString = header;
		this.x = x;
		this.y = y;
		this.theme = theme;
		this.typeButtons = new ArrayList();
		this.typePanels = new ArrayList();
		theme.mainPanelConstructor(this, x, y);
	}
	
	public void draw(float mouseX, float mouseY) {
		theme.mainPanelDraw(this, mouseX, mouseY);
	}
	
	public void mouseClicked(float mouseX, float mouseY, int state) {
		theme.mainPanelMouseClicked(this, mouseX, mouseY, state);
	}
	
	public void mouseReleased(float x, float y, int state) {
		theme.mainPanelMouseReleased(this, x, y, state);
	}
	
	public void keyPressed(int key) {
		theme.mainPanelKeyPress(this, key);
	}
	
}
