package me.robeart.raion.client.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.robeart.raion.client.Raion;
import me.robeart.raion.client.gui.cui.values.ListValueRenderer;
import me.robeart.raion.client.gui.cui.values.ValueRenderer;
import me.robeart.raion.client.util.font.Fonts;
import me.robeart.raion.client.util.font.MinecraftFontRenderer;

import java.util.List;

public class ListValue extends Value<String> {
	
	private List<String> list;
	private String value;
	
	public ListValue(String name, String value, List<String> list) {
		super(name);
		this.value = list.contains(value) ? value : list.get(0);
		this.list = list;
	}
	
	public ListValue(String name, String value, List<String> list, Value mainSetting) {
		super(name, mainSetting);
		this.value = list.contains(value) ? value : list.get(0);
		this.list = list;
	}
	
	public ListValue(String name, String value, List<String> list, Value mainSetting, String listValue) {
		super(name, mainSetting, listValue);
		this.value = list.contains(value) ? value : list.get(0);
		this.list = list;
	}
	
	@Override
	public ValueRenderer createRenderer() {
		return new ListValueRenderer(this, Fonts.INSTANCE.getFont40());
	}
	
	public void nextValue() {
		moveValue(1);
	}
	
	public void previousValue() {
		moveValue(-1);
	}
	
	public void moveValue(int offset) {
		int newIndex = list.indexOf(getValue()) + offset;
		while (newIndex < 0) {
			newIndex = list.size() + newIndex;
		}
		while (newIndex > list.size() - 1) {
			newIndex = newIndex - list.size();
		}
		setValue(list.get(newIndex));
	}
	
	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public void setValue(String input) {
		if (list.contains(input)) {
			this.value = list.get(list.indexOf(input));
			callback(value);
		}
	}
	
	public List<String> getOptions() {
		return list;
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
