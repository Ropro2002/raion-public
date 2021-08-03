package me.robeart.raion.client.gui.clickguirework;

/**
 * @author Robeart
 */
public class Component {
	
	private int x;
	private int y;
	private int width;
	private int heigth;
	
	private int red;
	private int green;
	private int blue;
	private int alpha;
	
	private boolean hovering;
	private boolean mouseDown;
	
	private int priority;
	
	private Component parent;
	
	public Component(int x, int y, int width, int heigth, int red, int green, int blue, int alpha, Component parent) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.heigth = heigth;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		this.parent = parent;
	}
	
	public void draw(int mouseX, int mouseY) {
	
	}
	
	public void mouseClicked(int mouseX, int mouseY, int state) {
	
	}
	
	public void mouseReleased(int mouseX, int mouseY, int state) {
	
	}
	
}
