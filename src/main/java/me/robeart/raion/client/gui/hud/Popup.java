package me.robeart.raion.client.gui.hud;

public interface Popup {
	
	void render();
	
	int getY();
	
	void setY(int y);
	
	int getTime();
	
	void setTime(int time);
	
	boolean getDestroy();
	
	void setDestroy(boolean destroy);
}
