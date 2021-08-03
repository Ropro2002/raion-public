package me.robeart.raion.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.robeart.raion.client.Raion;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.Configurable;
import me.robeart.raion.client.util.json.JsonUtil;
import me.robeart.raion.client.value.Value;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ModuleConfig implements Configurable {
	
	private Path configFile;
	
	public ModuleConfig(Path configFile) {
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
			if (!e.getValue().isJsonObject()) continue;
			Module module = Raion.INSTANCE.getModuleManager().getModule(e.getKey());
			if (module == null) continue;
			for (Map.Entry<String, JsonElement> e2 : e.getValue().getAsJsonObject().entrySet()) {
				if (e2.getKey().equalsIgnoreCase("Bind")) {
					if (!e2.getValue().isJsonPrimitive()) continue;
					JsonPrimitive primitive = e2.getValue().getAsJsonPrimitive();
					if (!primitive.isString()) continue;
					module.setBind(primitive.getAsString());
				}
				if (e2.getKey().equalsIgnoreCase("Visible")) {
					if (!e2.getValue().isJsonPrimitive()) continue;
					JsonPrimitive primitive = e2.getValue().getAsJsonPrimitive();
					if (!primitive.isBoolean()) continue;
					module.setVisible(primitive.getAsBoolean());
				}
				if (e2.getKey().equalsIgnoreCase("Enabled")) {
					if (!e2.getValue().isJsonPrimitive()) continue;
					JsonPrimitive primitive = e2.getValue().getAsJsonPrimitive();
					if (!primitive.isBoolean()) continue;
					module.setState(primitive.getAsBoolean());
				}
				for (Value value : module.getValues()) {
					if (e2.getKey().contains(".")) {
						if (value.getParentSetting() == null) continue;
						if (value.getName()
							.equalsIgnoreCase(e2.getKey()
								.substring(e2.getKey().lastIndexOf(".") + 1)) && value.getParentSetting()
							.getName()
							.equalsIgnoreCase(e2.getKey().substring(0, e2.getKey().lastIndexOf(".")))) {
							value.fromJson(e2.getValue());
						}
					}
					else if (value.getName().equalsIgnoreCase(e2.getKey())) {
						value.fromJson(e2.getValue());
					}
				}
			}
		}
		save();
		
	}
	
	@Override
	public void save() {
		JsonObject json = new JsonObject();
		for (Module module : Raion.INSTANCE.getModuleManager().getModuleList()) {
			JsonObject settings = new JsonObject();
			settings.add("Enabled", new JsonPrimitive(module.getState()));
			settings.add("Visible", new JsonPrimitive(module.getVisible()));
			settings.add("Bind", new JsonPrimitive(module.getBind()));
			if (!module.getValues().isEmpty()) for (Value value : module.getValues())
				settings.add(value.getParentSetting() == null ? value.getName() : value.getParentSetting()
					.getName() + '.' + value.getName(), value.toJson());
			json.add(module.getName(), settings);
		}
		try (BufferedWriter writer = Files.newBufferedWriter(configFile)) {
			JsonUtil.prettyGson.toJson(json, writer);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
