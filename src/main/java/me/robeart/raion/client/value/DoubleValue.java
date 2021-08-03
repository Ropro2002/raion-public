package me.robeart.raion.client.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.robeart.raion.client.Raion;
import me.robeart.raion.client.gui.cui.values.NumberValueRenderer;
import me.robeart.raion.client.gui.cui.values.ValueRenderer;
import me.robeart.raion.client.util.MathUtils;
import me.robeart.raion.client.util.font.Fonts;
import me.robeart.raion.client.util.font.MinecraftFontRenderer;

public class DoubleValue extends NumberValue<Double> {
	
	private double value;
	private double min;
	private double max;
	private double increment;
	
	
	public DoubleValue(String name, double value, double min, double max, double increment) {
		super(name);
		this.value = value;
		this.min = min;
		this.max = max;
		this.increment = increment;
	}
	
	public DoubleValue(String name, double value, double min, double max, double increment, Value mainSetting) {
		super(name, mainSetting);
		this.value = value;
		this.min = min;
		this.max = max;
		this.increment = increment;
	}
	
	public DoubleValue(String name, double value, double min, double max, double increment, Value mainSetting, String listValue) {
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
	public Double getValue() {
		return this.value;
	}
	
	@Override
	public void setValue(Double value) {
		value = Math.round(value / increment) * increment;
		String stringVal = "" + this.increment;
		int roundTo = stringVal.length() - stringVal.indexOf(".");
		value = MathUtils.round(value, roundTo);
		value = MathUtils.clamp(value, min, max);
		this.value = value;
		callback(value);
	}
	
	public float getPercentage() {
		return (((getValue().floatValue() - (float) getMin()) / (((float) getMax() - (float) getMin()))) * 100f);
	}
	
	public void setPercentage(float percentage) {
		float value = ((float) (getMax() - (float) getMin()) / 100f) * percentage;
		setValue((double) value + getMin());
	}
	
	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}
	
	public double getIncrement() {
		return increment;
	}
	
	@Override
	public void fromJson(JsonElement json) {
		if (!json.isJsonPrimitive()) return;
		JsonPrimitive primitive = json.getAsJsonPrimitive();
		if (!primitive.isNumber()) return;
		setValue(primitive.getAsDouble());
	}
	
	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(value);
	}
}
