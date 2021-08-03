package me.robeart.raion.client.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.robeart.raion.client.Raion;
import me.robeart.raion.client.gui.cui.values.NumberValueRenderer;
import me.robeart.raion.client.gui.cui.values.ValueRenderer;
import me.robeart.raion.client.util.MathUtils;
import me.robeart.raion.client.util.font.Fonts;
import me.robeart.raion.client.util.font.MinecraftFontRenderer;

public class FloatValue extends NumberValue<Float> {
	
	private float value;
	private float min;
	private float max;
	private float increment;
	
	
	public FloatValue(String name, float value, float min, float max, float increment) {
		super(name);
		this.value = value;
		this.min = min;
		this.max = max;
		this.increment = increment;
	}
	
	public FloatValue(String name, String description, float value, float min, float max, float increment) {
		super(name, description);
		this.value = value;
		this.min = min;
		this.max = max;
		this.increment = increment;
	}
	
	public FloatValue(String name, String description, float value, float min, float max, float increment, Value mainSetting) {
		super(name, description, mainSetting);
		this.value = value;
		this.min = min;
		this.max = max;
		this.increment = increment;
	}
	
	public FloatValue(String name, float value, float min, float max, float increment, Value mainSetting) {
		super(name, mainSetting);
		this.value = value;
		this.min = min;
		this.max = max;
		this.increment = increment;
	}
	
	public FloatValue(String name, float value, float min, float max, float increment, Value mainSetting, String listValue) {
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
	public Float getValue() {
		return this.value;
	}
	
	@Override
	public void setValue(Float value) {
		value = Math.round(value / increment) * increment;
		String stringVal = "" + this.increment;
		int roundTo = stringVal.length() - stringVal.indexOf(".");
		value = MathUtils.round(value, roundTo);
		value = MathUtils.clamp(value, min, max);
		this.value = value;
		callback(value);
	}
	
	public float getPercentage() {
		float percentage = (((getValue() - getMin()) / (getMax() - getMin()))) * 100f;
		return percentage;
	}
	
	public void setPercentage(float percentage) {
		float value = ((getMax() - getMin()) / 100f) * percentage;
		setValue(value + getMin());
	}
	
	public float getMin() {
		return min;
	}
	
	public float getMax() {
		return max;
	}
	
	public float getIncrement() {
		return increment;
	}
	
	@Override
	public void fromJson(JsonElement json) {
		if (!json.isJsonPrimitive()) return;
		JsonPrimitive primitive = json.getAsJsonPrimitive();
		if (!primitive.isNumber()) return;
		setValue(primitive.getAsFloat());
	}
	
	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(value);
	}
}
