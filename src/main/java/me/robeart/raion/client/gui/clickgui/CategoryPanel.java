package me.robeart.raion.client.gui.clickgui;

import me.robeart.raion.client.Raion;
import me.robeart.raion.client.module.Module;

import java.util.ArrayList;

public class CategoryPanel {
	public float x;
	public float y;
	public float listY;
	public float width;
	public float height;
	public float renderedHeight = -1;
	public float dragx;
	public float dragy;
	public float scrollY = 0;
	public boolean dragging;
	public boolean enabled;
	public CategoryButton categoryButton;
	public String headerString;
	public ArrayList<Button> buttonsActual;
	public ArrayList<Button> buttons;
	public Button bindingButton;
	public Module module;
	
	public CategoryPanel(String name, CategoryButton categoryButton, float x, float y) {
		this.x = x;
		this.y = y;
		this.width = 90;
		this.height = 0;
		this.dragx = 0;
		this.dragy = 0;
		this.dragging = false;
		this.enabled = true;
		this.categoryButton = categoryButton;
		this.headerString = name;
		this.buttonsActual = new ArrayList();
		this.buttons = buttonsActual;
		categoryButton.mainPanel.theme.categoryPanelConstructor(this, categoryButton, x, y);
	}
	
	public void draw(float mouseX, float mouseY) {
		Raion.INSTANCE.getGui().getTheme().categoryPanelDraw(this, mouseX, mouseY);
	}
	
	public void mouseClicked(float mouseX, float mouseY, int button) {
		Raion.INSTANCE.getGui().getTheme().categoryPanelMouseClicked(this, mouseX, mouseY, button);
	}
	
	public void mouseReleased(float mouseX, float mouseY, int button) {
		Raion.INSTANCE.getGui().getTheme().categoryPanelMouseReleased(this, mouseX, mouseY, button);
	}
}
