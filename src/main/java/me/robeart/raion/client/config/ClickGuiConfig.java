package me.robeart.raion.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.robeart.raion.client.Raion;
import me.robeart.raion.client.gui.clickgui.CategoryButton;
import me.robeart.raion.client.util.Configurable;
import me.robeart.raion.client.util.json.JsonUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ClickGuiConfig implements Configurable {
	
	private Path configFile;
	
	public ClickGuiConfig(Path configFile) {
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
			for (CategoryButton button : Raion.INSTANCE.getGui().getMainPanel().typeButtons) {
				if (button.categoryPanel.headerString.equalsIgnoreCase(e.getKey())) {
					for (Map.Entry<String, JsonElement> e2 : e.getValue().getAsJsonObject().entrySet()) {
						if (e2.getKey().equalsIgnoreCase("Enabled")) {
							if (!e2.getValue().isJsonPrimitive()) continue;
							JsonPrimitive primitive = e2.getValue().getAsJsonPrimitive();
							if (!primitive.isBoolean()) continue;
							button.enabled = primitive.getAsBoolean();
						}
						if (e2.getKey().equalsIgnoreCase("X")) {
							if (!e2.getValue().isJsonPrimitive()) continue;
							JsonPrimitive primitive = e2.getValue().getAsJsonPrimitive();
							if (!primitive.isNumber()) continue;
							button.categoryPanel.x = primitive.getAsInt();
						}
						if (e2.getKey().equalsIgnoreCase("Y")) {
							if (!e2.getValue().isJsonPrimitive()) continue;
							JsonPrimitive primitive = e2.getValue().getAsJsonPrimitive();
							if (!primitive.isNumber()) continue;
							button.categoryPanel.y = primitive.getAsInt();
						}
					}
				}
			}
		}
		save();
		
	}
	
	@Override
	public void save() {
		JsonObject json = new JsonObject();
		for (CategoryButton button : Raion.INSTANCE.getGui().getMainPanel().typeButtons) {
			JsonObject settings = new JsonObject();
			settings.add("Enabled", new JsonPrimitive(button.enabled));
			settings.add("X", new JsonPrimitive(button.categoryPanel.x));
			settings.add("Y", new JsonPrimitive(button.categoryPanel.y));
			json.add(button.categoryPanel.headerString, settings);
		}
		try (BufferedWriter writer = Files.newBufferedWriter(configFile)) {
			JsonUtil.prettyGson.toJson(json, writer);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
