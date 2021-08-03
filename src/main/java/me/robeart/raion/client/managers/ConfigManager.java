package me.robeart.raion.client.managers;

import me.robeart.raion.client.config.*;
import me.robeart.raion.client.gui.cui.RaionCui;
import me.robeart.raion.client.module.render.search.SearchModule;
import me.robeart.raion.client.util.Configurable;
import net.minecraft.client.Minecraft;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
	
	private List<Configurable> configs = new ArrayList<>();
	private Path mainPath;
	
	public ConfigManager() {
		this.mainPath = Minecraft.getMinecraft().gameDir.toPath().resolve("raion");
		try {
			Files.createDirectories(mainPath);
		}
		catch (Exception e) {
		}
		this.configs.add(new ModuleConfig(mainPath.resolve("modules.json")));
		this.configs.add(new ClickGuiConfig(mainPath.resolve("clickgui.json")));
		this.configs.add(new SettingsConfig(mainPath.resolve("settings.json")));
		//this.configs.add(new XrayConfig(mainPath.resolve("xray.json")));
		this.configs.add(new FriendConfig(mainPath.resolve("friends.json")));
		this.configs.add(RaionCui.INSTANCE);
		this.configs.add(SearchModule.INSTANCE);
		this.configs.add(MacroConfig.INSTANCE);
		//this.configs.add(WaypointManager.INSTANCE);
	}
	
	public void saveAll() {
		for (Configurable configurable : configs) {
			try {
				configurable.save();
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
	public void loadAll() {
		for (Configurable configurable : configs) {
			try {
				configurable.load();
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
}
