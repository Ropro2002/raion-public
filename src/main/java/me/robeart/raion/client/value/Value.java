package me.robeart.raion.client.value;

import com.google.gson.JsonElement;
import me.robeart.raion.client.gui.cui.values.ValueRenderer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class Value<T> {
	
	private String name;
	private String description;
	private Consumer<T> callback;
	private Value parent = null;
	private String listFilter;
	
	public Value(String name) {
		this(name, null, null, null);
	}
	
	public Value(String name, String description) {
		this(name, description, null, null);
	}
	
	public Value(String name, Value parentSetting) {
		this(name, null, parentSetting, null);
	}
	
	public Value(String name, String description, Value parentSetting) {
		this(name, description, parentSetting, null);
	}
	
	public Value(String name, Value parentSetting, String listFilter) {
		this(name, null, parentSetting, listFilter);
	}
	
	public Value(String name, String description, Value parentSetting, String listFilter) {
		this.name = name;
		this.description = description;
		this.parent = parentSetting;
		this.listFilter = listFilter;
	}
	
	public abstract ValueRenderer createRenderer();
	
	public String getName() {
		return name;
	}
	
	@Nullable
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Value getParentSetting() {
		return parent;
	}
	
	public String getListFilter() {
		return this.listFilter;
	}
	
	public abstract T getValue();
	
	public abstract void setValue(T newVal);
	
	public abstract void fromJson(JsonElement json);
	
	public abstract JsonElement toJson();
	
	public Value<T> setCallback(Consumer<T> callback) {
		this.callback = callback;
		return this;
	}
	
	protected void callback(T newVal) {
		if (callback != null) {
			callback.accept(newVal);
		}
	}
}
