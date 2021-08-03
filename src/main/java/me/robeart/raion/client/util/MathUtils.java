package me.robeart.raion.client.util;

import net.minecraft.client.Minecraft;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {
	public static final Color lime = new Color(0xbfff00);
	
	public static float[] getRgba(int colour) {
		float red = (float) (colour >> 16 & 255) / 255.0F;
		float green = (float) (colour >> 8 & 255) / 255.0F;
		float blue = (float) (colour & 255) / 255.0F;
		float alpha = (float) (colour >> 24 & 255) / 255.0F;
		return new float[]{red, green, blue, alpha};
	}
	
	public static Color getBlendedColor(float percentage) {
		return getBlendedColor(percentage, 255);
	}
	
	public static Color getBlendedColor(float percentage, int alpha) {
		if (percentage < 0.5) {
			return interpolate(Color.RED, Color.YELLOW, percentage / 0.5, alpha);
		}
		else {
			//return interpolate(Color.YELLOW, lime, (percentage - 0.5) / 0.5);
			return interpolate(Color.YELLOW, Color.GREEN, (percentage - 0.5) / 0.5, alpha);
		}
	}
	
	private static Color interpolate(Color color1, Color color2, double fraction, int alpha) {
		double r = interpolate(color1.getRed(), color2.getRed(), fraction);
		double g = interpolate(color1.getGreen(), color2.getGreen(), fraction);
		double b = interpolate(color1.getBlue(), color2.getBlue(), fraction);
		return new Color((int) Math.round(r), (int) Math.round(g), (int) Math.round(b), alpha);
	}
	
	private static double interpolate(double d1, double d2, double fraction) {
		return d1 + (d2 - d1) * fraction;
	}
	
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
			
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
	
	//I'd recommend using Math.toRadians() or Math.toDegrees()
	public static double radTodeg(double rad) {
		return rad * 57.295780181884766D;
	}
	
	public static double degToRad(double deg) {
		return deg * 0.01745329238474369D;
	}
	
	//This is the seppuku directionSpeed, I changed it because the earlier one wasn't working properly. If you'd rather I rewrite one from scratch just let me know.
	public static double[] directionSpeed(double speed) {
		final Minecraft mc = Minecraft.getMinecraft();
		float forward = mc.player.movementInput.moveForward;
		float side = mc.player.movementInput.moveStrafe;
		float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
		
		if (forward != 0) {
			if (side > 0) {
				yaw += (forward > 0 ? -45 : 45);
			}
			else if (side < 0) {
				yaw += (forward > 0 ? 45 : -45);
			}
			side = 0;
			
			if (forward > 0) {
				forward = 1;
			}
			else if (forward < 0) {
				forward = -1;
			}
		}
		
		final double sin = Math.sin(Math.toRadians(yaw + 90));
		final double cos = Math.cos(Math.toRadians(yaw + 90));
		final double posX = (forward * speed * cos + side * speed * sin);
		final double posZ = (forward * speed * sin - side * speed * cos);
		return new double[]{posX, posZ};
	}
	
	public static boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
			
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static int floor(float value) {
		int i = (int) value;
		return value < i ? i - 1 : i;
	}
	
	public static int floor(double value) {
		int i = (int) value;
		return value < i ? i - 1 : i;
	}
	
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	public static float round(float value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.floatValue();
	}
	
	public static int clamp(int num, int min, int max) {
		return num < min ? min : num > max ? max : num;
	}
	
	public static float clamp(float num, float min, float max) {
		return num < min ? min : num > max ? max : num;
	}
	
	public static double clamp(double num, double min, double max) {
		return num < min ? min : num > max ? max : num;
	}
}
