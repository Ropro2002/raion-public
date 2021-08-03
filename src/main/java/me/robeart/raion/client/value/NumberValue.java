package me.robeart.raion.client.value;

/**
 * @author cookiedragon234 17/Jun/2020
 */
public abstract class NumberValue <T extends Number> extends Value<T> {
	public NumberValue(String name) {
		super(name);
	}
	
	public NumberValue(String name, String description) {
		super(name, description);
	}
	
	public NumberValue(String name, Value parentSetting) {
		super(name, parentSetting);
	}
	
	public NumberValue(String name, String description, Value parentSetting) {
		super(name, description, parentSetting);
	}
	
	public NumberValue(String name, Value parentSetting, String listFilter) {
		super(name, parentSetting, listFilter);
	}
	
	public NumberValue(String name, String description, Value parentSetting, String listFilter) {
		super(name, description, parentSetting, listFilter);
	}
}
