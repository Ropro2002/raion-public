package me.cookiedragon234.falcon.authentication.hwid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HwidGetter {
	private final static String hostname;
	private final static String ethernetAddr;
	private final static String diskUUID;
	private final static String diskSN;
	private static String virtualOS;
	
	static {
		hostname = safeNull(HwidGetterImpl.getHostName());
		ethernetAddr = safeNull(HwidGetterImpl.getEthernetAddress());
		diskUUID = safeNull(HwidGetterImpl.getDiskUUID());
		diskSN = safeNull(HwidGetterImpl.getDiskSerialNumber());
		
		try {
			if (HwidGetterImpl.isRunningInVM()) {
				virtualOS = HwidGetterImpl.getVM();
			}
			else
				virtualOS = null;
		}
		catch (Exception e) {
			virtualOS = null;
			e.printStackTrace();
		}
	}
	
	@Nonnull
	private static String safeNull(@Nullable String s) {
		if (s == null)
			return "null";
		return s;
	}
	
	public static String getHostname() {
		return hostname;
	}
	
	public static String getEthernetAddr() {
		return ethernetAddr;
	}
	
	public static String getDiskUUID() {
		return diskUUID;
	}
	
	public static String getDiskSN() {
		return diskSN;
	}
	
	public static String getVirtualOS() {
		return virtualOS;
	}
}
