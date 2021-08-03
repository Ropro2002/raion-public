package me.robeart.raion.client.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

/**
 * Credit to https://gist.github.com/aadnk/0502e32369f203daaba9
 */
public class SkinManager {
	private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
	
	private static LoadingCache<String, String> profileCache = CacheBuilder.newBuilder().
		maximumSize(500).
		expireAfterWrite(4, TimeUnit.HOURS).
		build(new CacheLoader<String, String>() {
			public String load(String name) throws Exception {
				return getProfileJson(name);
			}
			
		});
	
	public static void updateSkin(GameProfile profile, String skinOwner) {
		try {
			String jsonStr = profileCache.get(skinOwner);
			JsonObject json = new JsonParser().parse(jsonStr).getAsJsonObject();
			JsonArray properties = json.get("properties").getAsJsonArray();
			for (JsonElement o : properties) {
				JsonObject property = o.getAsJsonObject();
				String name = property.get("name").getAsString();
				String value = property.get("value").getAsString();
				String signature = null;
				if (property.has("signature")) { // May be NULL
					signature = property.get("signature").getAsString();
				}
				
				profile.getProperties().removeAll(name);
				if (signature == null) {
					profile.getProperties().put(name, new Property(name, value));
				}
				else {
					profile.getProperties().put(name, new Property(name, value, signature));
				}
			}
		}
		catch (Exception e) {
			new RuntimeException("Cannot fetch profile for " + skinOwner, e).printStackTrace();
		}
	}
	
	private static String getProfileJson(String name) throws IOException {
		final URL url = new URL(PROFILE_URL + name + "?unsigned=false");
		URLConnection conn = url.openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 4.01; Windows NT)");
		return IOUtils.toString(conn.getInputStream());
	}
}
