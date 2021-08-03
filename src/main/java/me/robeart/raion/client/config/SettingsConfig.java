package me.robeart.raion.client.config;

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
import java.util.Map;

public class SettingsConfig implements Configurable {
	
	private Path configFile;
	
	public SettingsConfig(Path configFile) {
		this.configFile = configFile;
		load();
	}
	
	
	@Override
	public void load() {
		JsonObject json;
		try (BufferedReader reader = Files.newBufferedReader(configFile)) {
			json = JsonUtil.jsonParser.parse(reader).getAsJsonObject();
		}
		catch (IOException e) {
			save();
			return;
		}
		for (Map.Entry<String, JsonElement> e : json.entrySet()) {
			if (e.getKey().equalsIgnoreCase("prefix")) {
				if (!e.getValue().isJsonPrimitive()) continue;
				JsonPrimitive primitive = e.getValue().getAsJsonPrimitive();
				if (!primitive.isString()) continue;
				Raion.INSTANCE.getCommandManager().setPrefix(primitive.getAsString());
			}
		}
		save();
	}
	
	@Override
	public void save() {
		JsonObject json = new JsonObject();
		json.add("Prefix", new JsonPrimitive(Raion.INSTANCE.getCommandManager().getPrefix()));
		try (BufferedWriter writer = Files.newBufferedWriter(configFile)) {
			JsonUtil.prettyGson.toJson(json, writer);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
