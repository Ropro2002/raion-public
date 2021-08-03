package me.cookiedragon234.falcon;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.lang.reflect.Method;

@Mod(name = "Raion Loader", modid = "raionloader", version = "0.1", clientSideOnly = true)
public class RaionMod {
	public static Object raion = null;
	
	@Mod.EventHandler
	private void init(FMLInitializationEvent event) {
		try {
			raion.getClass().getDeclaredMethod("init", FMLInitializationEvent.class).invoke(raion, event);
		}
		catch (Exception e) {
			for (Method declaredMethod : raion.getClass().getDeclaredMethods()) {
				System.out.println(declaredMethod);
			}
			throw new IllegalStateException("init", e);
		}
	}
}
