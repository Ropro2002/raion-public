package me.cookiedragon234.falcon;

import com.sun.jna.Platform;
import kotlin.io.ByteStreamsKt;
import kotlin.io.FilesKt;
import net.minecraft.launchwrapper.Launch;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

public class NativeAccessor {
	public static final NativeAccessor INSTANCE = new NativeAccessor();
	
	static {
		try {
			if (!Platform.is64Bit()) {
				throw new IllegalStateException("Unsupported architecture: " + System.getProperty("os.arch"));
			}
			
			String suffix;
			if (Platform.isWindows()) {
				suffix = ".dll";
			} else if (Platform.isLinux()) {
				suffix = ".so";
			} else if (Platform.isMac()) {
				suffix = ".dylib";
			} else {
				throw new IllegalStateException("Unsupported operating system: '" + System.getProperty("os.name") + "'");
			}
			
			File libraryFile = new File(System.getenv("java.library.path"), "RaionNative" + suffix);
			if (libraryFile.exists()) {
				libraryFile.delete();
			}
			libraryFile.deleteOnExit();
			String resourceName = "/falcon/libRaionNative" + suffix;
			InputStream is = NativeAccessor.class.getResourceAsStream(resourceName);
			Objects.requireNonNull(is, "Native Classpath Stream (" + resourceName + ")");
			FilesKt.writeBytes(libraryFile, ByteStreamsKt.readBytes(is));
			
			System.load(libraryFile.getAbsolutePath());
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IllegalStateException("Error loading Raion Native Library", t);
		}
	}
	
	public static String getHwid(Object _unused1, Object _unused2, Object _unused3, Object _unused4) {
		return INSTANCE.d(_unused1, _unused2, _unused3, _unused4).replaceAll("[^A-Za-z0-9]", "");
	}
	
	public static void prepareTransformer(String msg, Object bytemap, Object _classloader, Object attachThread) {
		INSTANCE.b(msg, bytemap, _classloader, attachThread);
	}
	public static void println(Object msg) {
		INSTANCE.c(msg.toString());
	}
	
	public static void putLong(long addr, long val) {
		INSTANCE.o(addr, val, Launch.classLoader);
	}
	
	public static void decrypt(Object byteArray) {
		INSTANCE.k(byteArray);
	}
	
	
	/**
	 * @param u str: JString
	 * @param a byte_map: JObject
	 * @param c _class_loader: JObject
	 * @param d _redefinitions: JObject
	 */
	private native void b(String u, Object a, Object c, Object d);
	/**
	 * @param u str: JString
	 */
	private native void c(Object u);
	
	/**
	 * @return hwid
	 */
	public native String d(Object a, Object b, Object c, Object d);
	
	/**
	 * Put long
	 */
	public native void o(long a, long b, Object o);
	
	/**
	 * Decrypt bytearray
	 */
	public native void k(Object in);
}
