package me.robeart.raion.client.module.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.robeart.raion.client.events.events.player.LocateCapeEvent;
import me.robeart.raion.client.module.Module;
import me.robeart.raion.client.util.ChatUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.codec.digest.DigestUtils;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Robeart
 */
public class CapeModule extends Module {
	
	private Map<String, Integer> capes = null;
	
	public CapeModule() {
		super("Cape", "Shows a cape for people who have one", Category.RENDER, false);
	}
	
	@Override
	public void onEnable() {
		try {
			capes = getCapes();
		}
		catch (Throwable throwable) {
			capes = null;
		}
	}
	
	@Listener
	public void locateCape(LocateCapeEvent event) {
		if (capes == null) {
			ChatUtils.message("Unable to load capes");
			toggle();
			return;
		}
		String uuid = hashUUID(event.getUuid());
		if (capes.get(uuid) != null) {
			int cape = capes.get(uuid);
			event.setResourceLocation(new ResourceLocation("textures/cape/cape" + cape + ".png"));
			event.setCanceled(true);
		}
		
	}
	
	private String hashUUID(UUID uuid) {
		String plainUuid = uuid.toString().replace("-", "");
		return DigestUtils.sha1Hex(plainUuid);
	}
	
	private Map<String, Integer> getCapes() throws Throwable {
		URL url = new URL("https://raionclient.com/api/runtimeInfoApi.php");
		InputStream is = downloadFile(url);
		JsonObject element = new JsonParser().parse(new InputStreamReader(is)).getAsJsonObject();
		JsonObject capes = element.getAsJsonObject("capes");
		
		Map<String, Integer> out = new HashMap<>();
		for (Map.Entry<String, JsonElement> entry : capes.entrySet()) {
			out.put(entry.getKey(), entry.getValue().getAsInt());
		}
		return out;
	}
	
	private InputStream downloadFile(URL url) throws IOException {
		HttpsURLConnection httpConn = (HttpsURLConnection) url.openConnection();
		httpConn.setRequestProperty("User-Agent", "Raion Client Connect");
		int responseCode = httpConn.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			return httpConn.getInputStream();
		}
		else {
			throw new IllegalStateException("Server returned response code: " + responseCode);
		}
	}
	
}
