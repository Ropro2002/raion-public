package me.cookiedragon234.falcon.loading;

import me.robeart.raion.mixin.MixinLoader;
import net.minecraft.launchwrapper.IClassNameTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLRemappingAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.util.Arrays;

/**
 * @author cookiedragon234 30/Aug/2020
 */
public class DeobfuscationTransformer implements IClassTransformer, IClassNameTransformer {
	private static final String[] EXEMPT_LIBS = new String[] {
		"com.google.",
		"com.mojang.",
		"joptsimple.",
		"io.netty.",
		"it.unimi.dsi.fastutil.",
		"oshi.",
		"com.sun.",
		"com.ibm.",
		"paulscode.",
		"com.jcraft"
	};
	private static final String[] EXEMPT_DEV = new String[] {
		"net.minecraft.",
		"net.minecraftforge."
	};
	
	private static final boolean RECALC_FRAMES = Boolean.parseBoolean(System.getProperty("FORGE_FORCE_FRAME_RECALC", "false"));
	private static final int WRITER_FLAGS = ClassWriter.COMPUTE_MAXS | (RECALC_FRAMES ? ClassWriter.COMPUTE_FRAMES : 0);
	private static final int READER_FLAGS = RECALC_FRAMES ? ClassReader.SKIP_FRAMES : ClassReader.EXPAND_FRAMES;
	// COMPUTE_FRAMES causes classes to be loaded, which could cause issues if the classes do not exist.
	// However in testing this has not happened. {As we run post SideTransformer}
	// If reported we need to add a custom implementation of ClassWriter.getCommonSuperClass
	// that does not cause class loading.
	
	private final boolean deobfuscatedEnvironment = !MixinLoader.isObfuscatedEnvironment;
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (bytes == null) {
			return null;
		}
		
		if (!shouldTransform(name)) return bytes;
		
		try {
			ClassReader classReader = new ClassReader(bytes);
			ClassWriter classWriter = new ClassWriter(WRITER_FLAGS);
			FMLRemappingAdapter remapAdapter = new FMLRemappingAdapter(classWriter);
			classReader.accept(remapAdapter, READER_FLAGS);
			return classWriter.toByteArray();
		} catch (Throwable t) {
			t.printStackTrace();
			return bytes;
		}
	}
	
	private boolean shouldTransform(String name)
	{
		boolean transformLib = Arrays.stream(EXEMPT_LIBS).noneMatch(name::startsWith);
		
		if (deobfuscatedEnvironment)
		{
			return transformLib && Arrays.stream(EXEMPT_DEV).noneMatch(name::startsWith);
		}
		else
		{
			return transformLib;
		}
	}
	
	@Override
	public String remapClassName(String name)
	{
		return FMLDeobfuscatingRemapper.INSTANCE.map(name.replace('.','/')).replace('/', '.');
	}
	
	@Override
	public String unmapClassName(String name)
	{
		return FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/')).replace('/','.');
	}
	
}
