package me.cookiedragon234.falcon.authentication;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Processor;
import oshi.software.os.OperatingSystem;

import java.util.Objects;

/**
 * Utility class for generating hwids
 */
public class HwidBuilder {
	private static final HardwareAbstractionLayer hardwareAbstractionLayer;
	private static final OperatingSystem operatingSystem;
	private static final Processor processor;
	
	static {
		SystemInfo si = new SystemInfo();
		hardwareAbstractionLayer = si.getHardware();
		operatingSystem = si.getOperatingSystem();
		processor = hardwareAbstractionLayer.getProcessors()[0];
	}
	
	/**
	 * Add the display adapter name to the hwid eg. Geforce2
	 *
	 * @return The updated hwid builder
	 */
	public static String getDisplayAdapter() {
		String toAdd = "nonce";
		try {
			toAdd = Objects.requireNonNull(Display.getAdapter());
		}
		catch (Exception ignored) {
		}
		
		return toAdd;
	}
	
	/**
	 * Add the graphics vendor to the hwid
	 *
	 * @return The updated hwid builder
	 */
	public static String getGraphicsVendor() {
		String toAdd = "nonce";
		try {
			toAdd = Objects.requireNonNull(GL11.glGetString(GL11.GL_VENDOR));
		}
		catch (Exception ignored) {
		}
		
		return toAdd;
	}
	
	/**
	 * Add the operating system family to the hwid
	 *
	 * @return The updated hwid builder
	 */
	public static String getOsFamily() {
		String toAdd = "nonce";
		try {
			toAdd = Objects.requireNonNull(operatingSystem.getFamily());
		}
		catch (Exception ignored) {
		}
		
		return toAdd;
	}
	
	/**
	 * Add the os manufacturer to the hwid
	 *
	 * @return The updated hwid builder
	 */
	public static String getOsManufacturer() {
		String toAdd = "nonce";
		try {
			toAdd = Objects.requireNonNull(operatingSystem.getManufacturer());
		}
		catch (Exception ignored) {
		}
		
		return toAdd;
	}
	
	/**
	 * Add the processor name to the hwid eg. Intel(R) Core(TM)2 Duo CPU T7300 @ 2.00GHz
	 *
	 * @return The updated hwid builder
	 */
	public static String getProcessorName() {
		String toAdd = "nonce";
		try {
			toAdd = Objects.requireNonNull(processor.getName());
		}
		catch (Exception ignored) {
		}
		
		return toAdd;
	}
	
	/**
	 * Add the processor identifier to the hwid eg. x86 Family 6 Model 15 Stepping 10.
	 *
	 * @return The updated hwid builder
	 */
	public static String getProcessorIdentifier() {
		String toAdd = "nonce";
		try {
			toAdd = Objects.requireNonNull(processor.getIdentifier());
		}
		catch (Exception ignored) {
		}
		
		return toAdd;
	}
}
