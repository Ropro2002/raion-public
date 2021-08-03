package me.robeart.raion.client.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import me.robeart.raion.client.module.render.XrayModule;
import me.robeart.raion.client.util.Configurable;
import me.robeart.raion.client.util.json.JsonUtil;
import net.minecraft.block.Block;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

public class XrayConfig implements Configurable {
	//TODO Make an Array of the settings so it is not just one big copy pasta
	
	private Path configFile;
	
	public XrayConfig(Path configFile) {
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
			XrayModule.initblocks();
			save();
			return;
		}
		for (Map.Entry<String, JsonElement> e : json.entrySet()) {
			if (!e.getValue().isJsonPrimitive()) continue;
			JsonPrimitive primitive = e.getValue().getAsJsonPrimitive();
			if (!primitive.isString()) continue;
			Gson gson = new Gson();
			ArrayList<String> blocks = gson.fromJson(primitive.getAsString(), new TypeToken<ArrayList<String>>() {
			}.getType());
			if (e.getKey().equalsIgnoreCase("Blocks")) {
				for (String s : blocks) {
					XrayModule.blocks.add(Block.getBlockById(Integer.parseInt(s)));
				}
			}
		}
		save();
	}
	
	@Override
	public void save() {
		//TODO fix this ugly xray saving
		ArrayList<Integer> blocks = new ArrayList<>();
		for (Block b : XrayModule.blocks) blocks.add(Block.getIdFromBlock(b));
		String jsonblocks = new Gson().toJson(blocks);
		JsonObject json = new JsonObject();
		json.add("Blocks", new JsonPrimitive(jsonblocks));
		try (BufferedWriter writer = Files.newBufferedWriter(configFile)) {
			JsonUtil.prettyGson.toJson(json, writer);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
