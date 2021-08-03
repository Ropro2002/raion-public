package me.cookiedragon234.falcon.loading;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * @author cookiedragon234 10/Jun/2020
 */
public class RaionURL extends URLStreamHandler {
	byte[] resource;
	
	public RaionURL(byte[] resource) {
		this.resource = resource;
	}
	
	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		return null;
	}
}
