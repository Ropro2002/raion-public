package me.cookiedragon234.falcon.authentication.hwid;

public class ByteConverter {
	private static final char[] bytes;
	
	static {
		bytes = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	}
	
	public static String bytesToStr(final byte[] array) {
		final int length;
		final char[] array2 = new char[(length = array.length) << 1];
		int i = 0;
		int n = 0;
		while (i < length) {
			array2[n++] = bytes[(0xF0 & array[i]) >>> 4];
			array2[n++] = bytes[0xF & array[i]];
			++i;
		}
		return new String(array2);
	}
}
