package me.robeart.raion;

import me.cookiedragon234.falcon.NativeAccessor;
import me.robeart.raion.client.Raion;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * @author Robeart
 */
public class LoadClient {
	static {
		NativeAccessor.println("Load Client clinit");
	}
	
	public LoadClient() {
		NativeAccessor.println("Load Client initialization");
	}
	
	public void init(FMLInitializationEvent event) {
		Raion.INSTANCE.initClient();
	}
	
}
