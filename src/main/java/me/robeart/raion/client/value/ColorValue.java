package me.robeart.raion.client.value;

/**
 * @author Robeart
 */

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.robeart.raion.client.gui.cui.values.ValueRenderer;

import java.awt.*;
import java.util.ArrayList;

public class ColorValue extends Value<Color> {
	
	private Color value;
	private ArrayList<IntValue> colors;
	
	public ColorValue(String name, Color value) {
		this(name, value, true);
	}
	
	public ColorValue(String name, Color value, boolean alpha) {
		super(name);
		this.value = value;
		colors = new ArrayList<>();
		this.colors.add(new IntValue("Red", value.getRed(), 0, 255, 1));
		this.colors.add(new IntValue("Green", value.getGreen(), 0, 255, 1));
		this.colors.add(new IntValue("Blue", value.getBlue(), 0, 255, 1));
		if (alpha) {
			this.colors.add(new IntValue("Alpha", value.getAlpha(), 0, 255, 1));
		}
	}
	
	public ColorValue(String name, Color value, Value mainSetting) {
		super(name, mainSetting);
		this.value = value;
		colors = new ArrayList<>();
		this.colors.add(new IntValue("Red", value.getRed(), 0, 255, 1));
		this.colors.add(new IntValue("Green", value.getGreen(), 0, 255, 1));
		this.colors.add(new IntValue("Blue", value.getBlue(), 0, 255, 1));
		this.colors.add(new IntValue("Alpha", value.getAlpha(), 0, 255, 1));
	}
	
	public ColorValue(String name, Color value, Value mainSetting, String listValue) {
		super(name, mainSetting, listValue);
		this.value = value;
		colors = new ArrayList<>();
		this.colors.add(new IntValue("Red", value.getRed(), 0, 255, 1));
		this.colors.add(new IntValue("Green", value.getGreen(), 0, 255, 1));
		this.colors.add(new IntValue("Blue", value.getBlue(), 0, 255, 1));
		this.colors.add(new IntValue("Alpha", value.getAlpha(), 0, 255, 1));
	}
	
	@Override
	public ValueRenderer createRenderer() {
		return null;
	}
	
	@Override
	public Color getValue() {
		return this.value;
	}
	
	@Override
	public void setValue(Color value) {
		colors.get(0).setValue(value.getRed());
		colors.get(1).setValue(value.getGreen());
		colors.get(2).setValue(value.getBlue());
		if (colors.size() > 3) {
			colors.get(3).setValue(value.getAlpha());
		}
		this.value = value;
		callback(value);
	}
	
	public void setColor() {
		int alpha = 255;
		if (colors.size() > 3) {
			alpha = colors.get(3).getValue();
		}
		Color c = new Color(colors.get(0).getValue(), colors.get(1).getValue(), colors.get(2).getValue(), alpha);
		setValue(c);
	}
	
	@Override
	public void fromJson(JsonElement json) {
		if (!json.isJsonPrimitive()) return;
		JsonPrimitive primitive = json.getAsJsonPrimitive();
		if (!primitive.isNumber()) return;
		setValue(new Color(primitive.getAsInt()));
	}
	
	public ArrayList<IntValue> getColors() {
		return colors;
	}
	
	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(value.getRGB());
	}
}
