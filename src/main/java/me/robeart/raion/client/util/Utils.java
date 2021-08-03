package me.robeart.raion.client.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author cookiedragon234 21/Apr/2020
 */
public class Utils {
	/**
	 * Scale of 0-255
	 */
	public static int getRgb(int r, int g, int b, int a) {
		return ((a & 0xFF) << 24) |
			((r & 0xFF) << 16) |
			((g & 0xFF) << 8) |
			((b & 0xFF) << 0);
	}
	
	public static void setFinalStatic(Field field, Object newValue) throws Exception {
		setFinalStatic(field, null, newValue);
	}
	
	public static void setFinalStatic(Field field, Object instance, Object newValue) throws Exception {
		field.setAccessible(true);
		
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		
		field.set(instance, newValue);
	}
}
