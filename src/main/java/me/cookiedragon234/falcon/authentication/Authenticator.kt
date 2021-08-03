package me.cookiedragon234.falcon.authentication

import com.google.common.hash.Hashing
import com.google.gson.JsonObject
import me.cookiedragon234.falcon.NativeAccessor
import me.cookiedragon234.falcon.antidump.antis.AntiPaster
import me.cookiedragon234.falcon.loading.Loader
import net.minecraft.launchwrapper.Launch
import net.minecraftforge.fml.client.FMLClientHandler
import org.apache.http.client.utils.URIBuilder
import java.awt.BorderLayout
import java.awt.Frame
import java.awt.GridLayout
import java.beans.XMLDecoder
import java.beans.XMLEncoder
import java.io.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.Hashtable
import javax.swing.*
import javax.swing.WindowConstants.DISPOSE_ON_CLOSE
import kotlin.concurrent.thread


/**
 * @author cookiedragon234 03/Mar/2020
 */
@Throws(IOException::class)
fun getAuthenticationPayload(args: Map<String, String>): JsonObject {
	try {
		//val hwid = Hwid.create().hash().toString()
		val hwid = Hashing.sha512().hashString(
			NativeAccessor.getHwid(
				Launch.classLoader, FMLClientHandler.instance(), Runtime.getRuntime(), Thread.currentThread()
			), StandardCharsets.UTF_8
		).toString()
		val uuid = getUuid(args)
		var attempts = 0
		var credentials: Map<String, String>? = try {
			loadCredentials(hwid)
		} catch (e: Throwable) {
			e.printStackTrace()
			promptForCredentials(hwid, attempts).also { attempts += 1 }
		}
		while (credentials == null || credentials["user"] == null || credentials["pass"] == null || !checkLogin(credentials)) {
			credentials = promptForCredentials(hwid, attempts, credentials != null)
			attempts += 1
		}
		val payload = createRequestPayload(
			uuid,
			hwid,
			credentials["user"]!!,
			credentials["pass"]!!
		)
		timestamp(payload)
		payload.addProperty("e", Loader.protocolVersion)
		return payload
	} catch (t: Throwable) {
		throw RuntimeException("Error creating an authentication payload", t)
	}
}

fun getUuid(args: Map<String, String>): String {
	try {
		for ((key, value) in args) {
			if (key == "--uuid") {
				NativeAccessor.println("UUID: $value")
				return value
			}
		}
	} catch (e: java.lang.Exception) {
		NativeAccessor.println("Could not fetch your UUID! If you are using a new account other raion users will not see your cape.")
		e.printStackTrace()
	}
	return "#"
}

@Throws(IOException::class)
fun promptForCredentials(hwid: String, attempt: Int = 0, failed: Boolean = false): Map<String, String> {
	val logininformation = hashMapOf<String, String>()
	val panel = JPanel(BorderLayout(5, 5))
	if (failed) {
		val label = JPanel(GridLayout(1, 1, 2, 2))
		label.add(JLabel("Incorrect username/password (#$attempt)", SwingConstants.CENTER))
		panel.add(label, BorderLayout.CENTER)
	}
	val label = JPanel(GridLayout(0, 1, 2, 2))
	label.add(JLabel("Username", SwingConstants.RIGHT))
	label.add(JLabel("Password", SwingConstants.RIGHT))
	panel.add(label, BorderLayout.WEST)
	val controls = JPanel(GridLayout(0, 1, 2, 2))
	val username = JTextField()
	controls.add(username)
	val password = JPasswordField()
	controls.add(password)
	panel.add(controls, BorderLayout.CENTER)
	val frame = Frame()
	frame.isAlwaysOnTop = true
	val res = JOptionPane.showConfirmDialog(
		frame,
		panel,
		"Raion Loader",
		JOptionPane.OK_CANCEL_OPTION,
		JOptionPane.PLAIN_MESSAGE
	)
	if (res == JOptionPane.OK_OPTION) {
		logininformation["user"] = username.text
		logininformation["pass"] = String(password.password)
	} else {
		AntiPaster.eleminateThePaster()
	}
	saveCredentials(hwid, logininformation)
	return logininformation
}

val ivFile = File("raion/credentials.cookiedragon")
val credentialsFile = File("raion/credentials.verysecure")
val oldCredentialsFile = File("raion/credentials.xml")

private val ivSize = 128 / 8

@Throws(Throwable::class)
private fun saveCredentials(hwid: String, credentials: Map<String, String>) {
	val byteOut = ByteArrayOutputStream()
	XMLEncoder(byteOut).use {
		it.writeObject(credentials)
	}
	val text = byteOut.toByteArray().toString(Charset.defaultCharset())
	credentialsFile.writeText(encrypt(text, hwid))
}

@Throws(Throwable::class)
private fun loadCredentials(hwid: String): Map<String, String> {
	if (!credentialsFile.exists() || ivFile.exists()) {
		if (ivFile.exists()) {
			ivFile.delete()
		}
		if (oldCredentialsFile.exists()) {
			return XMLDecoder(oldCredentialsFile.inputStream()).use {
				it.readObject() as Hashtable<String, String>
			}.also { oldCredentialsFile.delete(); saveCredentials(hwid, it) }
		}
		throw FileNotFoundException()
	} else {
		val decrypted = encrypt(credentialsFile.readText(), hwid)
		if (decrypted.isBlank()) {
			credentialsFile.delete()
			throw IllegalStateException("Corrupt credential file")
		}
		return XMLDecoder(decrypted.byteInputStream()).use {
			it.readObject() as Map<String, String>
		}
	}
}

private inline fun encrypt(text: String, password: String): String = buildString {
	for (i in text.indices) {
		append(text[i] xor password[i % password.length])
	}
}

private infix fun Char.xor(c: Char): Char = (this.toInt() xor c.toInt()).toChar()

private fun createRequestPayload(uuid: String, hwid: String, username: String, password: String): JsonObject {
	return JsonObject().apply {
		addProperty("b", uuid)
		addProperty("a", hwid)
		addProperty("c", username)
		addProperty("f", password)
	}
}

private fun createLoginPayload(username: String?, password: String?): JsonObject {
	return JsonObject().apply {
		addProperty("Username", username)
		addProperty("Password", password)
	}
}

@Throws(IOException::class)
private fun checkLogin(credentials: Map<String, String>): Boolean {
	/*val dialog: JDialog = JDialog(null as Frame?, "Loading...", true).apply {
		isAlwaysOnTop = true
		isUndecorated = true
		defaultCloseOperation = DISPOSE_ON_CLOSE
		
		val messagePane = JPanel()
		messagePane.add(JLabel("Please wait while we log you in"))
		contentPane.add(messagePane)
		
		isVisible = true
	}*/
	val payload = createLoginPayload(credentials["user"]!!, credentials["pass"]!!)
	val payloadText = Base64.getEncoder().encode(payload.toString().toByteArray(Charsets.UTF_8)).toString(Charsets.UTF_8)
	val url = URIBuilder("https://raionclient.com/api/login.php")
		.addParameter("data", payloadText)
		.build().toURL()
	val httpConn = url.openConnection()
	httpConn.setRequestProperty("User-Agent", "Raion Client Connect")
	BufferedReader(InputStreamReader(httpConn.inputStream)).use {
		//dialog.apply {
		//	isVisible = false
		//	dispose()
		//}
		val lines = it.readLines()
		return if (lines.any { line -> line.equals("OK", true) }) {
			true
		} else {
			NativeAccessor.println("Error: $lines")
			false
		}
	}
}

private fun timestamp(json: JsonObject): JsonObject {
	return json.apply {
		addProperty("h", System.currentTimeMillis())
	}
}
