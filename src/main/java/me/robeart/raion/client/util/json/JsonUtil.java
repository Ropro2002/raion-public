package me.robeart.raion.client.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public final class JsonUtil {
	public static final Gson gson = new Gson();
	public static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
	public static final JsonParser jsonParser = new JsonParser();
}
