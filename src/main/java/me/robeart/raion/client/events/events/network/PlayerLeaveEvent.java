package me.robeart.raion.client.events.events.network;

import com.mojang.authlib.GameProfile;

/**
 * @author Robeart
 */
public class PlayerLeaveEvent {
	
	private GameProfile gameProfile;
	
	public PlayerLeaveEvent(GameProfile gameProfile) {
		this.gameProfile = gameProfile;
	}
	
	public GameProfile getGameProfile() {
		return gameProfile;
	}
	
	public void setGameProfile(GameProfile gameProfile) {
		this.gameProfile = gameProfile;
	}
	
}
