package me.robeart.raion.client.managers;

import com.mojang.authlib.GameProfile;
import me.robeart.raion.client.module.misc.BetterTabModule;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashSet;
import java.util.Set;

public class FriendManager {
	
	// these are all the developer usernames I know feel free to modify
	private static String[] developers = new String[]{
		"cookiedragon234",
		"cpacketkeepalive",
		"robeartt",
		"spacketkeepalive",
		"paralusion",
		"itsoktofly",
		"hobbled"
	};
	private Set<String> friendList = new HashSet<>();
	
	public FriendManager() {
	}
	
	public boolean add(String name) {
		BetterTabModule.INSTANCE.markPlayerListDirty();
		name = name.toLowerCase().trim();
		return friendList.add(name);
	}
	
	public boolean remove(String name) {
		BetterTabModule.INSTANCE.markPlayerListDirty();
		name = name.toLowerCase().trim();
		return friendList.remove(name);
	}
	
	public boolean isFriend(GameProfile profile) {
		String name = profile.getName().toLowerCase().trim();
		return friendList.contains(name);
	}
	
	public boolean isFriend(String name) {
		name = name.toLowerCase().trim();
		return friendList.contains(name);
	}
	
	public boolean isFriend(EntityPlayer e) {
		String name = e.getName().toLowerCase().trim();
		return friendList.contains(name);
	}
	
	public boolean isDeveloper(String name) {
		name = name.toLowerCase().trim();
		
		for (String developer : developers) {
			if (developer.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public Set<String> getFriendList() {
		return friendList;
	}
	
	public void setFriendList(Set<String> friendList) {
		this.friendList = friendList;
	}
}
