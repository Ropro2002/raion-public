package me.robeart.raion.client.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.robeart.raion.client.Raion;
import me.robeart.raion.client.gui.cui.values.BooleanValueRenderer;
import me.robeart.raion.client.gui.cui.values.ValueRenderer;
import me.robeart.raion.client.module.ClickGuiModule;
import me.robeart.raion.client.util.Interpolation;
import me.robeart.raion.client.util.font.Fonts;
import me.robeart.raion.client.util.font.MinecraftFontRenderer;
import net.minecraft.client.Minecraft;

public class BooleanValue extends Value<Boolean> {
	
	private static final Minecraft mc = Minecraft.getMinecraft();
	private boolean value;
	private float overlayAlpha;
	
	public BooleanValue(String name, boolean value) {
		super(name);
		this.value = value;
	}
	public BooleanValue(String name, String description, boolean value) {
		super(name, description);
		this.value = value;
	}
	
	public BooleanValue(String name, boolean value, Value mainSetting) {
		super(name, mainSetting);
		this.value = value;
	}
	public BooleanValue(String name, String description, boolean value, Value mainSetting) {
		super(name, description, mainSetting);
		this.value = value;
	}
	
	public BooleanValue(String name, boolean value, Value mainSetting, String listValue) {
		super(name, mainSetting, listValue);
		this.value = value;
	}
	
	public BooleanValue(String name, String description, boolean value, Value mainSetting, String listValue) {
		super(name, description, mainSetting, listValue);
		this.value = value;
	}
	
	public int getAlphaOverlay() {
		float desired;
		if (value) {
			desired = 40;
		}
		else {
			desired = 0;
		}
		overlayAlpha = Interpolation.finterpTo(overlayAlpha, desired, mc.getRenderPartialTicks(), ClickGuiModule.INSTANCE
			.getColourSpeed());
		return (int) overlayAlpha;
	}
	
	@Override
	public ValueRenderer createRenderer() {
		return new BooleanValueRenderer(this, Fonts.INSTANCE.getFont40());
	}
	
	@Override
	public Boolean getValue() {
		return value;
	}
	
	@Override
	public void setValue(Boolean value) {
		this.value = value;
		callback(value);
	}
	
	@Override
	public void fromJson(JsonElement json) {
		if (!json.isJsonPrimitive()) return;
		JsonPrimitive primitive = json.getAsJsonPrimitive();
		if (!primitive.isBoolean()) return;
		setValue(primitive.getAsBoolean());
	}
	
	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(value);
	}
}
