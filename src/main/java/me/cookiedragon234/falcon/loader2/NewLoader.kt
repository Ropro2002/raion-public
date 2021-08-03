package me.cookiedragon234.falcon.loader2

import com.google.gson.Gson
import me.cookiedragon234.falcon.NativeAccessor
import me.cookiedragon234.falcon.antidump.CookieFuckery
import me.cookiedragon234.falcon.antidump.CookieFuckery.checkForJavaAgents
import me.cookiedragon234.falcon.antidump.CookieFuckery.disableJavaAgents
import me.cookiedragon234.falcon.antidump.CookieFuckery.printPID
import me.cookiedragon234.falcon.antidump.CookieFuckery.setPackageNameFilter
import me.cookiedragon234.falcon.antidump.CookieFuckery.shutdownListener
import me.cookiedragon234.falcon.antidump.CustomTransformerList
import me.cookiedragon234.falcon.antidump.StructFucker
import me.cookiedragon234.falcon.authentication.getAuthenticationPayload
import me.cookiedragon234.falcon.loading.Loader
import net.minecraft.launchwrapper.IClassTransformer
import net.minecraft.launchwrapper.Launch
import net.minecraft.launchwrapper.LaunchClassLoader
import net.minecraftforge.fml.common.FMLLog
import org.spongepowered.asm.lib.tree.ClassNode
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.mixin.Mixins
import org.spongepowered.asm.mixin.transformer.Config
import org.spongepowered.asm.service.*
import org.spongepowered.asm.util.ReEntranceLock
import java.awt.Frame
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.net.URL
import java.net.URLEncoder
import java.security.CodeSource
import java.security.SecureClassLoader
import java.time.Duration
import java.time.Instant
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.swing.JOptionPane


/**
 * @author cookiedragon234 26/Apr/2020
 */
object NewLoader {
	fun load(args: Map<String, String>) {
		try {
			time {
				val oldAgent = System.getProperty("http.agent")
				try {
					System.setProperty("http.agent", "Raion Client Connect")
					
					@Suppress("UNCHECKED_CAST")
					val launchCache = LaunchClassLoader::class.java.getDeclaredField("resourceCache").let {
						it.isAccessible = true
						it.get(Launch.classLoader) as MutableMap<String, ByteArray>
					}
					
					val dir = File("raion")
					if (!dir.exists()) dir.mkdir()
					Loader.authPayload = getAuthenticationPayload(args)
					val classUrl = URL(
						"https://raionclient.com/api/request.php?data=" + URLEncoder.encode(
							Loader.authPayload.toString(),
							"UTF-8"
						)
					)
					
					
					shutdownListener()
					if (Loader.runningFromIntelliJ() || launchCache.containsKey("me/robeart/raion/client/command/Command.class")) {
						println("Not loading due to running in debugging environment")
						// We still need to peform the request just to update hwid/uuid
						if (false) {
							Loader.downloadFile(classUrl).close()
							return@time
						}
					} else {
						StructFucker.disassembleStruct()
						printPID()
						checkForJavaAgents()
						disableJavaAgents()
						setPackageNameFilter()
					}
					
					val classes = HashMap<String, ByteArray>()
					
					val bytes = Loader.downloadFile(classUrl).readBytes()
					ZipInputStream(ByteArrayInputStream(bytes)).use { stream ->
						var zipEntry: ZipEntry? = stream.nextEntry
						while (zipEntry != null) {
							if (!zipEntry.isDirectory && zipEntry.name.contains(".class")) {
								classes[toInternal(zipEntry.name)] = stream.readBytes()
							}
							
							zipEntry = stream.nextEntry
						}
					}
					
					if (classes.isEmpty()) {
						if (bytes.size < 500) {
							error(String(bytes))
						} else {
							error("Server didnt provide classes for unknown reason")
						}
					}
					
					
					val transformersField = LaunchClassLoader::class.java.getDeclaredField("transformers").also {
						it.isAccessible = true
					}
					val transformers = transformersField[Launch.classLoader] as MutableList<IClassTransformer>
					
					val deDummyTransformer = RaionTransformer(classes)
					transformers.add(deDummyTransformer)
					transformersField[Launch.classLoader] = CustomTransformerList(transformers, deDummyTransformer)
					
					for ((internal, value) in classes) {
						if (internal.contains("mixin")) {
							launchCache[internal] = value
						} else {
							launchCache[internal] = CookieFuckery.createDummyClass(internal)
						}
					}
				} finally {
					if (oldAgent != null) {
						System.setProperty("http.agent", oldAgent)
					} else {
						System.clearProperty("http.agent")
					}
				}
			}.also {
				println("Raion finished loading (${it.toMillis()}ms)")
			}
		} catch (t: Throwable) {
			NativeAccessor.println("Error while loading raion")
			t.printStackTrace()
			val frame = Frame()
			frame.isAlwaysOnTop = true
			frame.state = Frame.ICONIFIED
			JOptionPane.showConfirmDialog(
				frame,
				"Exception while loading raion '${t.message}'\nPlease send the contents of .minecraft/logs/latest.log to one of the developers",
				"Raion Exception",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE
			)
			Runtime.getRuntime().exit(-1)
		}
	}
	
	private fun toInternal(className: String): String {
		return className.removeSuffix(".class").replace('/', '.')
	}
	
	private fun time(block: () -> Unit): Duration {
		val start = Instant.now()
		block()
		return Duration.between(start, Instant.now())
	}
}

private fun replaceMixinConfig(name: String, bytes: ByteArray) {
	Launch.classLoader.getResource(name)?.also {
		it.openConnection().getOutputStream().use { os ->
			os.write(bytes)
		}
	} ?: error("Raion couldnt override mixin config")
}

private fun injectMixinConfig(name: String, bytes: ByteArray) {
	val mixinConfig = Class.forName("org.spongepowered.asm.mixin.transformer.MixinConfig")
	
	val config: Config = try {
		val service = MixinService.getService()
		val config = Gson().fromJson(
			String(bytes).reader(),
			mixinConfig
		)
		
		if (mixinConfig.getDeclaredMethod(
				"onLoad",
				IMixinService::class.java,
				String::class.java,
				MixinEnvironment::class.java
			).let {
				it.isAccessible = true
				it.invoke(config, MixinService.getService(), name, MixinEnvironment.getDefaultEnvironment())
			} as Boolean
		) {
			mixinConfig.getDeclaredMethod("getHandle").let {
				it.isAccessible = true
				it.invoke(config) as Config
			}
		} else error("Couldnt load mixin config")
	} catch (ex: Exception) {
		ex.printStackTrace()
		error("Couldnt load mixin config")
	}
	
	Mixins::class.java.getDeclaredMethod("registerConfiguration", Config::class.java).let {
		it.isAccessible = true
		it.invoke(null, config)
	}
}

private class RaionTransformer(val classes: Map<String, ByteArray>): IClassTransformer {
	override fun transform(name: String, transformedName: String?, bytes: ByteArray?): ByteArray? {
		return classes[name] ?: bytes
	}
}

private class RaionClassloader(
	val launchClassLoader: LaunchClassLoader,
	val parentZ: ClassLoader,
	val classMap: MutableMap<String, ByteArray>,
	val scl: ClassLoader,
	val resourceCache: MutableMap<String, ByteArray>,
	val cachedClasses: MutableMap<String, Class<*>>,
	val exemptions: MutableSet<String>
): ClassLoader(parentZ) {
	val method = SecureClassLoader::class.java.getDeclaredMethod(
		"defineClass",
		String::class.java,
		ByteArray::class.java,
		Integer.TYPE,
		Integer.TYPE,
		CodeSource::class.java
	).also {
		it.isAccessible = true
	}
	
	@Synchronized
	override fun loadClass(name: String): Class<*> {
		var excluded = false
		for (exclusion in exemptions) {
			if (name.startsWith(exclusion)) {
				excluded = true
				break
			}
		}
		
		if (!excluded) {
			return Launch.classLoader.loadClass(name)
		}
		
		try {
			return parentZ.loadClass(name)
		} catch (t: Throwable) {
			t.printStackTrace()
		}
		
		try {
			cachedClasses[name]?.let {
				return it
			}
		} catch (t: Throwable) {
			t.printStackTrace()
		}
		
		try {
			classMap.remove(name)?.let { classArr ->
				//return CookieFuckery.unsafe.defineAnonymousClass(Any::class.java, classArr, arrayOf())
				//return CookieFuckery.unsafe.defineClass(name, classArr, 0, classArr.size, Launch.classLoader, null)
				//return defineClass(name, classArr, 0, classArr.size)
				return (method.invoke(launchClassLoader, name, classArr, 0, classArr.size, null) as Class<*>).also {
					cachedClasses[name] = it
				}
			}
		} catch (t: Throwable) {
			t.printStackTrace()
		}
		
		error("Class not found $name")
	}
}

private class RaionMixinService(val parent: IMixinService, val classes: Map<String, ByteArray>): IMixinService {
	val lazyBytecodeProvider by lazy { RaionBytecodeProvider(parent.bytecodeProvider, classes) }
	override fun getBytecodeProvider(): IClassBytecodeProvider = lazyBytecodeProvider
	
	override fun checkEnv(bootSource: Any?) = parent.checkEnv(bootSource)
	override fun getSideName(): String = parent.sideName
	override fun prepare() = parent.prepare()
	override fun isClassLoaded(className: String?): Boolean = parent.isClassLoaded(className)
	override fun getTransformers(): MutableCollection<ITransformer> = parent.transformers
	override fun getInitialPhase(): MixinEnvironment.Phase = parent.initialPhase
	override fun getName(): String = parent.name
	override fun getPlatformAgents(): MutableCollection<String> = parent.platformAgents
	override fun init() = parent.init()
	override fun beginPhase() = parent.beginPhase()
	override fun getReEntranceLock(): ReEntranceLock = parent.reEntranceLock
	override fun getClassProvider(): IClassProvider = parent.classProvider
	override fun getClassRestrictions(className: String?): String = parent.getClassRestrictions(className)
	override fun getResourceAsStream(name: String?): InputStream = parent.getResourceAsStream(name)
	override fun isValid(): Boolean = parent.isValid
	override fun registerInvalidClass(className: String?) = parent.registerInvalidClass(className)
}

private class RaionBytecodeProvider(
	val parent: IClassBytecodeProvider,
	val classes: Map<String, ByteArray>
): IClassBytecodeProvider {
	override fun getClassBytes(name: String?, transformedName: String?): ByteArray =
		parent.getClassBytes(name, transformedName)
	
	override fun getClassNode(name: String?): ClassNode = parent.getClassNode(name)
	override fun getClassBytes(name: String?, runTransformers: Boolean): ByteArray {
		return classes[name] ?: parent.getClassBytes(name, runTransformers)
	}
}

private class DecryptingInputStream(private val inputStream: InputStream): InputStream() {
	override fun read(): Int {
		return inputStream.read() xor 9213
	}
	
	override fun close() = inputStream.close()
	override fun available() = inputStream.available()
	override fun skip(n: Long) = inputStream.skip(n)
	override fun reset() = inputStream.reset()
}
