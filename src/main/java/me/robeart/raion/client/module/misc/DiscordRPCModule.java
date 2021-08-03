package me.robeart.raion.client.module.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.Timer;
import me.robeart.raion.client.util.json.ISerializable;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author cookiedragon234
 */
public class DiscordRPCModule extends Module {
	private static final String APP_ID = "609789062953435271";
	private static final DiscordRPC rpc = DiscordRPC.INSTANCE;
	private static final File cfgFile = new File("raion/discordrpc.json");
	
	private static DiscordEventHandlers handlers = new DiscordEventHandlers();
	private static DiscordRichPresence presence = null;
	private static RpcSettings rpcSettings = null;
	
	public DiscordRPCModule() {
		super("DiscordRPC", "Allows people to see your ingame status next to your Discord name", Category.MISC);
		
		if (cfgFile.exists()) {
			try {
				FileReader fileReader = new FileReader(cfgFile);
				JsonObject jsonObject = new JsonParser().parse(fileReader).getAsJsonObject();
				
				if (jsonObject.size() >= 6) {
					rpcSettings = new RpcSettings(jsonObject);
				}
				fileReader.close();
			}
			catch (Exception ignored) {
			}
		}
		
		if (rpcSettings == null) {
			// Make default values
			rpcSettings = new RpcSettings(
				"In Main Menu",
				"$discord",
				"SinglePlayer",
				"with $health hp",
				"$ip",
				"In the $dimension"
			);
		}
		
		startup();
	}
	
	public static RpcSettings getRpcSettings() {
		return rpcSettings;
	}
	
	/**
	 * @return The current {@link DiscordRichPresence} with relevant data for the current user
	 */
	private static DiscordRichPresence getPresence(DiscordRichPresence last) {
		Map<String, String> gameState = new HashMap<>();
		ServerData serverData = getCurrentServer();
		
		gameState.put("ip", serverData.serverIP);
		gameState.put("version", serverData.gameVersion);
		gameState.put("players", serverData.populationInfo);
		gameState.put("motd", serverData.serverMOTD);
		gameState.put("svrname", serverData.serverName);
		gameState.put("ping", String.valueOf(serverData.pingToServer));
		gameState.put("coords", getCoords());
		gameState.put("health", String.valueOf(getHealth()));
		gameState.put("dimension", mc.player == null ? "none" : mc.player.dimension == -1 ? "Nether" : mc.player.dimension == 0 ? "Overworld" : "End");
		gameState.put("item", mc.player == null ? "none" : mc.player.getHeldItemMainhand().getDisplayName());
		gameState.put("discord", "discord.gg/EpFFVE5");
		gameState.put("discordShort", "discord/EpFFVE5");
		gameState.put("discordCode", "EpFFVE5");
		
		String state;
		String desc;
		if (mc.world == null) {
			state = rpcSettings.mmState;
			desc = rpcSettings.mmDesc;
		}
		else {
			if (mc.isSingleplayer()) {
				state = rpcSettings.spState;
				desc = rpcSettings.spDesc;
			}
			else {
				state = rpcSettings.mpState;
				desc = rpcSettings.mpDesc;
			}
		}
		
		state = RpcSettings.format(state, gameState);
		desc = RpcSettings.format(desc, gameState);
		
		DiscordRichPresence richPresence = new DiscordRichPresence();
		
		// Presence hasnt changed
		if (last != null && (last.details.equals(state) && last.state.equals(desc))) {
			return null;
		}
		richPresence.startTimestamp = System.currentTimeMillis();
		
		richPresence.details = state;
		richPresence.state = desc;
		richPresence.largeImageKey = "logo";
		richPresence.largeImageText = "Raion";
		
		return richPresence;
	}
	
	private static String getCoords() {
		BlockPos pos;
		if (mc.player != null) {
			pos = mc.player.getPosition();
		}
		else {
			pos = new BlockPos(0, 0, 0);
		}
		
		return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
	}
	
	private static int getHealth() {
		if (mc.player != null) {
			return Math.round(mc.player.getHealth());
		}
		return 0;
	}
	
	private static ServerData getCurrentServer() {
		if (!mc.isSingleplayer()) {
			ServerData svr = mc.getCurrentServerData();
			
			if (svr != null) {
				return svr;
			}
		}
		return new ServerData("SinglePlayer", "localhost", true);
	}
	
	private boolean needsToClear = false;
	private Timer configTimer = new Timer();
	public void startup() {
		rpc.Discord_Initialize(APP_ID, handlers, true, "");
		Runtime.getRuntime().addShutdownHook(new Thread(rpc::Discord_Shutdown));
		
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(
			this::updateRPC,
			0, 10, TimeUnit.SECONDS
		);
	}
	
	private void updateRPC() {
		try {
			if (this.getState()) {
				needsToClear = true;
				rpc.Discord_RunCallbacks();
				
				DiscordRichPresence newPrecense = getPresence(presence);
				if (newPrecense != null) {
					presence = newPrecense;
					rpc.Discord_UpdatePresence(presence);
				}
			}
			else {
				if (needsToClear) {
					needsToClear = false;
					rpc.Discord_ClearPresence();
				}
			}
			if (configTimer.passed(240000)) { // 4 minutes
				configTimer.reset();
				// Save config file
				FileWriter fileWriter = new FileWriter(cfgFile);
				JsonObject jsonObject = rpcSettings.serialize();
				
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				fileWriter.write(gson.toJson(jsonObject));
				fileWriter.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static class RpcSettings implements ISerializable {
		public String mmState;
		public String mmDesc;
		public String spState;
		public String spDesc;
		public String mpState;
		public String mpDesc;
		
		public RpcSettings(JsonObject jsonObject) {
			this.deserialize(jsonObject);
		}
		
		public RpcSettings(String mmState, String mmDesc, String spState, String spDesc, String mpState, String mpDesc) {
			this.mmState = mmState;
			this.mmDesc = mmDesc;
			this.spState = spState;
			this.spDesc = spDesc;
			this.mpState = mpState;
			this.mpDesc = mpDesc;
		}
		
		public static String format(String unformatted, final Map<String, String> gameState) {
			for (Map.Entry<String, String> entry : gameState.entrySet()) {
				if (entry.getValue() == null) entry.setValue("null");
				
				unformatted = unformatted.replace("$" + entry.getKey(), entry.getValue());
			}
			
			return unformatted;
		}
		
		@Override
		public JsonObject serialize() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("mmState", mmState);
			jsonObject.addProperty("mmDesc", mmDesc);
			jsonObject.addProperty("spState", spState);
			jsonObject.addProperty("spDesc", spDesc);
			jsonObject.addProperty("mpState", mpState);
			jsonObject.addProperty("mpDesc", mpDesc);
			return jsonObject;
		}
		
		@Override
		public void deserialize(JsonObject jsonObject) {
			mmState = jsonObject.get("mmState").getAsString();
			mmDesc = jsonObject.get("mmDesc").getAsString();
			spState = jsonObject.get("spState").getAsString();
			spDesc = jsonObject.get("spDesc").getAsString();
			mpState = jsonObject.get("mpState").getAsString();
			mpDesc = jsonObject.get("mpDesc").getAsString();
		}
	}
}
