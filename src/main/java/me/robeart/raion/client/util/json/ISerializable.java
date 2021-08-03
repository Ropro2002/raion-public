package me.robeart.raion.client.util.json;

import com.google.gson.JsonObject;

/**
 * @author cookiedragon234 11/Nov/2019
 */
public interface ISerializable {
	JsonObject serialize();
	
	void deserialize(JsonObject jsonObject);
}
