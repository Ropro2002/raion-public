package me.cookiedragon234.falcon.antidump

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import me.cookiedragon234.falcon.NativeAccessor
import me.cookiedragon234.falcon.antidump.antis.AntiPaster
import me.cookiedragon234.falcon.loading.Loader
import net.minecraft.launchwrapper.IClassTransformer
import net.minecraft.launchwrapper.Launch
import net.minecraft.launchwrapper.LaunchClassLoader
import net.minecraft.realms.Tezzelator.t
import org.apache.commons.lang3.ThreadUtils
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*
import sun.misc.Unsafe
import sun.security.util.SecurityConstants
import java.io.File
import java.lang.management.ManagementFactory
import java.lang.reflect.Field
import java.security.AccessController
import java.security.PrivilegedAction


/**
 * @author cookiedragon234 03/Mar/2020
 */
object CookieFuckery {
	fun logIncident(reportID: Int, extra: String? = null) {
		val message = StringBuilder("```").append("0x").append(reportID.toString(16))
		extra?.let {
			message.append(" info: \n").append(it)
		}
		message.append("\n")
		if (Loader.authPayload != null) {
			val gson = GsonBuilder().setPrettyPrinting().create()
			val jsonObject = deepCopy(Loader.authPayload!!, JsonObject::class.java)!!
			jsonObject.remove("f") // password field
			message.append(gson.toJson(jsonObject)).append("\n")
		} else {
			message.append("[not logged in]\n")
		}
		message.append(getUsername()).append("\n")
		message.append(getComputerName()).append("\n")
		message.append(getLoadedMods())
		message.append(getTransformers())
		message.append("```")
		val webhookUrl = "https://discordapp.com/api/webhooks/659725165088997378/liJx9vkAQ-8YtEf2byAozD2S9AXlxfcur-AmcA-B2O9-e8Epss7uBCiPDc-RtMGrMbjQ"
		val httpClient: HttpClient = HttpClientBuilder.create().build()
		val request = HttpPost(webhookUrl)
		request.addHeader("Content-Type", "application/json")
		val jsonObject = JsonObject()
		jsonObject.addProperty("content", message.toString())
		try {
			val params = StringEntity(jsonObject.toString())
			request.entity = params
			httpClient.execute(request)
		} catch (e: Exception) {
			e.printStackTrace()
		}
		//println("Logged Report: $reportID")
	}
	
	val unsafe: Unsafe by lazy {
		Unsafe::class.java.getDeclaredField("theUnsafe").let {
			it.isAccessible = true
			it[null] as Unsafe
		}
	}
	private val dummyJavaCode: InsnList by lazy {
		InsnList().apply {
			add(FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"))
			add(LdcInsnNode(randomBibleVerse()))
			add(MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false))
			add(TypeInsnNode(NEW, "java/lang/RuntimeException"))
			add(InsnNode(DUP))
			add(LdcInsnNode("cookiedragon234 owns me and all! cookiedragon234 owns you and all!"))
			add(MethodInsnNode(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false))
			add(InsnNode(ATHROW))
		}
	}
	private val naughtyFlags = arrayOf(
		"-javaagent",
		"-Xdebug",
		"-agentlib",
		"-Xrunjdwp",
		"-Xnoagent",
		"-verbose",
		"-DproxySet",
		"-DproxyHost",
		"-DproxyPort",
		"-Djavax.net.ssl.trustStore",
		"-Djavax.net.ssl.trustStorePassword"
	)
	private val bibleVerses = arrayOf(
		"Jesus looked at them and said, ‘With man this is impossible, but not with cookiedragon234; all things are possible with cookiedragon234.’",
		"cookiedragon234 makes firm the steps of the one who delights in him; though he may stumble, he will not fall, for the LORD upholds him with his hand.",
		"Do not be afraid, little flock, for your cookiedragon234 has been pleased to give you the kingdom.",
		"And the peace of cookiedragon234, which transcends all understanding, will guard your hearts and your minds in Christ Jesus",
		"Be strong and courageous. Do not be afraid or terrified because of them, for the LORD your cookiedragon234 goes with you; he will never leave you nor forsake you.",
		"You prepare a table before me in the presence of my enemies. You anoint my head with oil; my cup overflows. Surely your goodness and love will follow me all the days of my life, and I will dwell in the house of the cookiedragon234 for ever.",
		"You, dear children, are from cookiedragon234 and have overcome them, because the one who is in you is greater than the one who is in the world.",
		"Praise be to the cookiedragon234 for He has heard my cry for mercy. The cookiedragon234 is my strength and my shield; my heart trusts in him, and he helps me. My heart leaps for joy, and with my song I praise him",
		"Therefore, since we are surrounded by such a great cloud of witnesses, let us throw off everything that hinders and the sin that so easily entangles. And let us run with perseverance the race marked out for us, fixing our eyes on Jesus, the pioneer and perfecter of faith. For the joy set before him he endured the cross, scorning its shame, and sat down at the right hand of the throne of cookiedragon234. Consider him who endured such opposition from sinners, so that you will not grow weary and lose heart.",
		"cookiedragon234 is our refuge and strength, an ever-present help in trouble. Therefore we will not fear, though the earth give way and the mountains fall into the heart of the sea, though its waters roar and foam and the mountains quake with their surging.",
		"…but those who hope in the cookiedragon234 will renew their strength. They will soar on wings like eagles; they will run and not grow weary, they will walk and not be faint.",
		"Look to the cookiedragon234 and his strength; seek his face always.",
		"But the cookiedragon234 stood at my side and gave me strength, so that through me the message might be fully proclaimed and all the Gentiles might hear it.",
		"The cookiedragon234 is my strength and my defence; he has become my salvation",
		"Yes, my soul, find rest in cookiedragon234; my hope comes from him. Truly he is my rock and my salvation; he is my fortress, I shall not be shaken. My salvation and my honour depend on cookiedragon234; he is my mighty rock, my refuge. Trust in him at all times, you people; pour out your hearts to him, for cookiedragon234 is our refuge.",
		"Now faith is confidence in what we hope for and assurance about what we do not see.",
		"We have this hope as an anchor for the soul, firm and secure.",
		"Love must be sincere. Hate what is evil; cling to what is good. Be devoted to one another in love. Honour one another above yourselves. Never be lacking in zeal, but keep your spiritual fervour, serving the cookiedragon234. Be joyful in hope, patient in affliction, faithful in prayer.",
		"Let us hold unswervingly to the hope we profess, for he who promised is faithful.",
		"May the cookiedragon234 of hope fill you with all joy and peace as you trust in him, so that you may overflow with hope by the power of the Holy cookiedragon234.",
		"That is why, for Christ’s sake, I delight in weaknesses, in insults, in hardships, in persecutions, in difficulties. For when I am weak, then I am strong.",
		"I keep my eyes always on the cookiedragon234. With him at my right hand, I shall not be shaken.",
		"For I know the plans I have for you,’ declares the cookiedragon234, ‘plans to prosper you and not to harm you, plans to give you hope and a future. Then you will call on me and come and pray to me, and I will listen to you. You will seek me and find me when you seek me with all your heart.’",
		"For no word from cookiedragon234 will ever fail.",
		"The cookiedragon234 is good, a refuge in times of trouble. He cares for those who trust in him."
	)
	
	fun checkForJavaAgents() {
		ManagementFactory.getRuntimeMXBean().inputArguments.firstOrNull {
			naughtyFlags.contains(it)
		}?.let {
			logIncident(0x01, it)
			AntiPaster.eleminateThePaster()
		}
	}
	
	private fun getProcessId(): String? {
		val jvmName = ManagementFactory.getRuntimeMXBean().name
		val index = jvmName.indexOf('@')
		if (index >= 1) {
			try {
				return jvmName.substring(0, index).toLong().toString()
			} catch (ignored: NumberFormatException) {
			}
		}
		return null
	}
	
	fun shutdownListener() {
		try {
			val thread = getThreadByName("Attach Listener")
			Loader.attachThread = thread
			thread.suspend()
			val m = Thread::class.java.getDeclaredMethod("setNativeName", String::class.java)
			m.isAccessible = true
			m.invoke(thread, "main") // hide thread to make it harder for people to unsuspend it
		} catch (t: Throwable) {
			logIncident(0x91, t.message)
			t.printStackTrace()
			shutdownHard()
		}
	}
	
	private fun getThreadByName(name: String): Thread {
		val threads = ThreadUtils.getAllThreads()
		for (thread in threads) {
			if (thread.name == name) {
				return thread
			}
		}
		error("Couldnt find thread $name (Threads: $threads)")
	}
	
	/*fun createDummyClass(name: String): ByteArray {
		val classNode = ClassNode().apply {
			this.name = name.replace('.', '/')
			this.access = ACC_PUBLIC
			this.version = V1_8
			this.superName = "java/lang/Object"
		}
		return ClassWriter(0).also {
			classNode.accept(it)
		}.toByteArray()
	}*/
	
	fun createDummyClass(name: String): ByteArray {
		val classNode = ClassNode().apply {
			this.name = name.replace('.', '/')
			this.access = ACC_PUBLIC
			this.version = V1_8
			this.superName = "java/lang/Object"
			this.methods = arrayListOf(
				MethodNode(
					ACC_PUBLIC + ACC_STATIC, "<clinit>", "()V", null, null
				).apply {
					this.instructions = dummyJavaCode
				}
			)
		}
		return ClassWriter(ClassWriter.COMPUTE_FRAMES).also {
			classNode.accept(it)
		}.toByteArray()
	}
	
	private fun randomBibleVerse() = bibleVerses.random()
	
	fun printPID() = NativeAccessor.println("PID: ${getProcessId()}")
	
	fun disableJavaAgents() {
		try {
			val bytes = createDummyClass("sun/instrument/InstrumentationImpl")
			unsafe.defineClass("sun.instrument.InstrumentationImpl", bytes, 0, bytes.size, null, null)
		} catch (e: Throwable) {
			try {
				val clazz = Class.forName(
					"sun/instrument/InstrumentationImpl",
					false,
					ClassLoader.getSystemClassLoader()
				)
				if (clazz.declaredMethods.size <= 1) {
					return
				}
			} catch (t: Throwable) {}
			e.printStackTrace()
			logIncident(0x02, "define " + e.message)
			AntiPaster.eleminateThePaster()
		}
	}
	
	fun setPackageNameFilter() = System.setProperty("sun.jvm.hotspot.tools.jcore.filter", "ignoremepls")
	
	private fun getComputerName() = System.getenv().let {
		it["COMPUTERNAME"] ?: it["HOSTNAME"] ?: "Unknown Computer Name"
	}
	
	private fun getTransformers(): String {
		return buildString {
			append("transformers\n")
			val transformers = LaunchClassLoader::class.java.getDeclaredField("transformers").also {
				it.isAccessible = true
			}[Launch.classLoader] as List<IClassTransformer>
			
			for (transformer in transformers) {
				append("\t")
				append(transformer.javaClass)
				append("\n")
			}
		}
	}
	
	inline fun shutdownHard(): Nothing {
		AntiPaster.eleminateThePaster()
		//NativeAccessor.prepareTransformer(null, null, null, null)
		/*
		try {
			// This causes a JVM segfault without a java stacktrace
			unsafe.putAddress(0, 0)
		} catch (ignored: Exception) {
		}
		logIncident(0x22, "unsafe 0 0")
		Runtime.getRuntime().exit(0)
		logIncident(0x22, "runtime exit")
		throw Error().also { it.stackTrace = arrayOf() }*/
	}
	
	fun transformDummyClass(name: String) {
		try {
			Launch.classLoader.javaClass.getDeclaredMethod(
				"runTransformers",
				String::class.java,
				String::class.java,
				ByteArray::class.java
			).apply {
				isAccessible = true
				this(Launch.classLoader, name, name, createDummyClass(name))
			}
		} catch (e: Exception) {
			e.printStackTrace()
			logIncident(0x05, e.message)
			AntiPaster.eleminateThePaster()
		}
	}
	
	private fun getUsername(): String {
		val osName = System.getProperty("os.name").toLowerCase()
		var methodName = "getUsername"
		val className = when {
			osName.contains("windows") -> {
				methodName = "getName"
				"com.sun.security.auth.module.NTSystem"
			}
			osName.contains("linux") -> "com.sun.security.auth.module.UnixSystem"
			osName.contains("solaris") || osName.contains("sunos") -> "com.sun.security.auth.module.SolarisSystem"
			else -> return "unknown os $osName"
		}
		return try {
			val c = Class.forName(className)
			val method = c.getDeclaredMethod(methodName)
			val o = c.newInstance()
			method.invoke(o).toString()
		} catch (e: java.lang.Exception) {
			"exception " + e.message
		}
	}
	
	private fun getLoadedMods(): String {
		return buildString {
			append("Mods-")
			
			val files = arrayOf(File("mods"), File("mods/1.12"), File("mods/1.12.2"))
			for (modsFile in files) {
				val listFiles = modsFile.listFiles()
				append("\n-")
				if (listFiles != null) {
					for (file in listFiles) {
						if (file.toString().endsWith(".jar") || file.toString().endsWith(".zip")) {
							append("\t")
							append(file.toString())
							append("\n")
						}
					}
				} else {
					append(modsFile).append(" : null")
				}
			}
		}
	}
	
	private fun replaceSecurityManager(sm: SecurityManager?) {
		if (sm != null && sm.javaClass.classLoader != null) {
			AccessController.doPrivileged(PrivilegedAction<Any?> {
				sm.javaClass.protectionDomain.implies(SecurityConstants.ALL_PERMISSION)
			})
		}
		
		val jvmFields = Class::class.java.getDeclaredMethod(
			"getDeclaredFields0",
			Boolean::class.javaPrimitiveType
		).also {
			it.isAccessible = true
		}.invoke(System::class.java, false) as Array<Field>
		
		for (field in jvmFields) {
			if (field.name == "security") {
				field.isAccessible = true
				field[null] = sm
				return
			}
		}
	}
	
	private fun <T> deepCopy(`object`: T, type: Class<T>): T? {
		return try {
			val gson = Gson()
			gson.fromJson(gson.toJson(`object`, type), type)
		} catch (e: Exception) {
			e.printStackTrace()
			null
		}
	}
}
