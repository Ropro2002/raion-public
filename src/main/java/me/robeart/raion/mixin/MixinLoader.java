package me.robeart.raion.mixin;

import me.cookiedragon234.falcon.RaionMod;
import me.cookiedragon234.falcon.loading.Loader;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

public class MixinLoader implements IFMLLoadingPlugin {
	
	public static boolean isObfuscatedEnvironment = false;
	
	public MixinLoader() {
		try {
			isObfuscatedEnvironment = Launch.classLoader.getClassBytes("net.minecraft.world.World") == null;
			
			File mcDir;
			{
				Field mcDirF = CoreModManager.class.getDeclaredField("mcDir");
				mcDirF.setAccessible(true);
				mcDir = (File) mcDirF.get(null);
			}
			
			String debfuscationDataName;
			{
				Method debfuscationDataNameM = FMLInjectionData.class.getDeclaredMethod("debfuscationDataName");
				debfuscationDataNameM.setAccessible(true);
				debfuscationDataName = (String) debfuscationDataNameM.invoke(null);
			}
			
			FMLDeobfuscatingRemapper.INSTANCE.setup(mcDir, Launch.classLoader, debfuscationDataName);
			
			Objects.requireNonNull(Launch.blackboard, "Launch.blackboard illegally null");
			Object launchArgs = Launch.blackboard.get("launchArgs");
			if (launchArgs == null) {
				launchArgs = Launch.blackboard.get("forgeLaunchArgs");
			}
			Objects.requireNonNull(launchArgs, "Couldnt retrieve launch args");
			Map<String, String> castedArgs = Objects.requireNonNull((Map<String, String>) launchArgs, "Couldnt cast launch args");
			try {
				Loader.loadClasses(castedArgs);
				//FMLCommonHandler.instance().exitJava(0, false);
				//NewLoader.INSTANCE.load(castedArgs);
			}
			catch (Throwable e) {
				throw new IllegalStateException("Failed to load classes!", e);
			}
			
			try {
				RaionMod.raion = Launch.classLoader.findClass("me.robeart.raion.LoadClient").newInstance();
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
			
			MixinBootstrap.init();
			Mixins.addConfiguration("mixins.raion.json");
			MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[0];
	}
	
	@Override
	public String getModContainerClass() {
		return null;
	}
	
	@Nullable
	@Override
	public String getSetupClass() {
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) {
		isObfuscatedEnvironment = (boolean) (Boolean) data.get("runtimeDeobfuscationEnabled");
	}
	
	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}

