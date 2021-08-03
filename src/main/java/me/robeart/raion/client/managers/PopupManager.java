package me.robeart.raion.client.managers;

import me.robeart.raion.client.gui.hud.Popup;

import java.util.ArrayList;
import java.util.List;

public class PopupManager {
	
	private List<Popup> popups = new ArrayList<>();
	
	public PopupManager() {
	}
	
	public void add(Popup popup) {
		for (Popup p : popups) {
			p.setY(p.getY() + 20);
		}
		popups.add(popup);
	}
	
	public void onRender() {
		for (int i = popups.size() - 1; i >= 0; i--) {
			Popup p = popups.get(i);
			if (p.getDestroy()) {
				popups.remove(p);
			}
			else {
				p.setTime(p.getTime() - 1);
				p.render();
			}
		}
	}
	
	
}
