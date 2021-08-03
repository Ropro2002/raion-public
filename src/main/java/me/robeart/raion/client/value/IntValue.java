package me.robeart.raion.client.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.robeart.raion.client.Raion;
import me.robeart.raion.client.gui.cui.values.NumberValueRenderer;
import me.robeart.raion.client.gui.cui.values.ValueRenderer;
import me.robeart.raion.client.util.MathUtils;
import me.robeart.raion.client.util.font.Fonts;
import me.robeart.raion.client.util.font.MinecraftFontRenderer;

public class IntValue extends NumberValue<Integer> {
	
	private int value;
	private int min;
	private int max;
	private int increment;
	
	
	public IntValue(String name, int value, int min, int max, int increment) {
		super(name);
		this.value = value;
		this.min = min;
		this.max = max;
		this.increment = increment;
	}
	
	public IntValue(String name, String description, int value, int min, int max, int increment) {
		super(name, description);
		this.value = value;
		this.min = min;
		this.max = max;
		this.increment = increment;
	}
	
	public IntValue(String name, String description, int value, int min, int max, int increment, Value mainSetting) {
		super(name, description, mainSetting);
		this.value = value;
		this.min = min;
		this.max = max;
		this.increment = increment;
	}
	
	public IntValue(String name, int value, int min, int max, int increment, Value mainSetting) {
		super(name, mainSetting);
		this.value = value;
		this.min = min;
		this.max = max;
		this.increment = increment;
	}
	
	public IntValue(String name, int value, int min, int max, int increment, Value mainSetting, String listValue) {
		super(name, mainSetting, listValue);
		this.value = value;
		this.min = min;
		this.max = max;
		this.increment = increment;
	}
	
	@Override
	public ValueRenderer createRenderer() {
		return new NumberValueRenderer(this, Fonts.INSTANCE.getFont40());
	}
	
	@Override
	public Integer getValue() {
		return this.value;
	}
	
	@Override
	public void setValue(Integer value) {
		int value2 = MathUtils.clamp(Math.round(value / (float) increment) * increment, min, max);
		this.value = value2;
		callback(value2);
	}
	
	public float getPercentage() {
		float percentage = ((((float) getValue() - (float) getMin()) / ((float) getMax() - (float) getMin()))) * 100f;
		return percentage;
	}
	
	public void setPercentage(float percentage) {
		float value = (((float) getMax() - (float) getMin()) / 100f) * percentage;
		setValue((int) value + getMin());
	}
	
	public int getMin() {
		return this.min;
	}
	
	public int getMax() {
		return this.max;
	}
	
	public int getIncrement() {
		return this.increment;
	}
	
	@Override
	public void fromJson(JsonElement json) {
		if (!json.isJsonPrimitive()) return;
		JsonPrimitive primitive = json.getAsJsonPrimitive();
		if (!primitive.isNumber()) return;
		setValue(primitive.getAsInt());
	}
	
	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(value);
	}
}
