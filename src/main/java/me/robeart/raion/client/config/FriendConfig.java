package me.robeart.raion.client.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.robeart.raion.client.Raion;
import me.robeart.raion.client.util.Configurable;
import me.robeart.raion.client.util.json.JsonUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class FriendConfig implements Configurable {
	
	private Path configFile;
	
	public FriendConfig(Path configFile) {
		this.configFile = configFile;
		load();
	}
	
	@Override
	public void load() {
		try {
			JsonObject json;
			try (BufferedReader reader = Files.newBufferedReader(configFile)) {
				json = JsonUtil.jsonParser.parse(reader).getAsJsonObject();
			}
			catch (IOException e) {
				save();
				return;
			}
			json.entrySet().forEach(entry -> {
				for (JsonElement e : entry.getValue().getAsJsonArray()) {
					if (!e.isJsonPrimitive()) continue;
					JsonPrimitive primitive = e.getAsJsonPrimitive();
					if (!primitive.isString()) continue;
					Raion.INSTANCE.getFriendManager().add(primitive.getAsString());
				}
			});
			save();
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	@Override
	public void save() {
		try {
			JsonObject json = new JsonObject();
			JsonArray array = new JsonArray();
			for (String s : Raion.INSTANCE.getFriendManager().getFriendList()) {
				array.add(s);
			}
			json.add("Friends", array);
			try (BufferedWriter writer = Files.newBufferedWriter(configFile)) {
				JsonUtil.prettyGson.toJson(json, writer);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
