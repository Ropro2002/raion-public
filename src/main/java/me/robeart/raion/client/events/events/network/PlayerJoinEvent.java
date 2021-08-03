package me.robeart.raion.client.events.events.network;

import com.mojang.authlib.GameProfile;

/**
 * @author Robeart
 */
public class PlayerJoinEvent {
	
	private GameProfile gameProfile;
	
	public PlayerJoinEvent(GameProfile gameProfile) {
		this.gameProfile = gameProfile;
	}
	
	public GameProfile getGameProfile() {
		return gameProfile;
	}
	
	public void setGameProfile(GameProfile gameProfile) {
		this.gameProfile = gameProfile;
	}
	
}

