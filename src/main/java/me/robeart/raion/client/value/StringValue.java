package me.robeart.raion.client.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.robeart.raion.client.gui.cui.values.ValueRenderer;

/**
 * @author Robeart
 */
public class StringValue extends Value<String> {
	
	private String value;
	
	public StringValue(String name, String value) {
		super(name);
		this.value = value;
	}
	
	public StringValue(String name, String value, Value mainSetting) {
		super(name, mainSetting);
		this.value = value;
	}
	
	public StringValue(String name, String value, Value mainSetting, String listValue) {
		super(name, mainSetting, listValue);
		this.value = value;
	}
	
	@Override
	public ValueRenderer createRenderer() {
		return null;//return new NumberValueRenderer(this, new MinecraftFontRenderer(Raion.INSTANCE.getFont().deriveFont(40f), true));
	}
	
	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public void setValue(String value) {
		this.value = value;
		callback(value);
	}
	
	@Override
	public void fromJson(JsonElement json) {
		if (!json.isJsonPrimitive()) return;
		JsonPrimitive primitive = json.getAsJsonPrimitive();
		if (!primitive.isString()) return;
		setValue(primitive.getAsString());
	}
	
	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(value);
	}
}
