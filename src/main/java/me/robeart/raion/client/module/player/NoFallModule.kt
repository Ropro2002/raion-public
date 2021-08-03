package me.robeart.raion.client.module.player

import me.robeart.raion.client.events.events.network.HandleDisconnectionEvent
import me.robeart.raion.client.events.events.network.PacketSendEvent
import me.robeart.raion.client.events.events.player.OnUpdateEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.ChatUtils
import me.robeart.raion.client.value.ListValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiDisconnected
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.multiplayer.ServerAddress
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.network.NetHandlerLoginClient
import net.minecraft.client.resources.I18n
import net.minecraft.entity.Entity
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.NetworkManager
import net.minecraft.network.handshake.client.C00Handshake
import net.minecraft.network.login.client.CPacketLoginStart
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.client.event.GuiScreenEvent
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener
import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException
import kotlin.concurrent.thread


/**
 * @author cookiedragon234 21/May/2020
 */
object NoFallModule: Module("NoFall", "Prevent yourself from taking fall damage", Category.PLAYER) {
	private val mode by ValueDelegate(ListValue("Mode", "Packet", arrayListOf("Packet", "Disconnect", "Reconnect")))
	
	val mainMenu by lazy { GuiMainMenu() }
	var connecting = false
	var dontDisconnect = false
	
	fun onNewScreen(event: GuiOpenEvent) {
		/*val gui = event.gui
		if (connecting) {
			println("New Screen: " + gui::class.java)
		}
		if (gui == null || gui !is GuiConnecting || gui !is GuiReconnecting) {
			connecting = false
		}*/
	}
	
	fun drawScreen(event: GuiScreenEvent.DrawScreenEvent) {
		/*val gui = event.gui
		if (connecting && (gui is GuiConnecting || gui is GuiReconnecting)) {
			event.isCanceled = true
			println("Cancelled ${gui::class.java}")
		} else {
			if (connecting) {
				println(gui::class.java)
			}
		}*/
	}
	
	var serverDataCache: ServerData? = null
	
	@Listener
	fun onDisconnect(event: HandleDisconnectionEvent) {
		if (dontDisconnect) event.isCanceled = true
	}
	
	@Listener
	fun onUpdate(event: OnUpdateEvent) {
		if (mode == "Reconnect" || mode == "Disconnect") {
			//println(mc.player.fallDistance)
			if (mc.player.fallDistance >= 5f) {
				val down = mc.player.positionVector.subtract(0.0, 5.0, 0.0)
				if (doesBBIntersect(mc.player, down)) {
					if (mode == "Disconnect") {
						mc.connection?.networkManager?.closeChannel(TextComponentString("No Fall Quit"))
								?: ChatUtils.error("Couldnt DC")
					} else {
						connecting = true
						mc.player.fallDistance = 0f
						val serverData = mc.currentServerData ?: serverDataCache!!
						serverDataCache = serverData
						mc.connection?.networkManager?.closeChannel(TextComponentString("No Fall Quit"))
							?: ChatUtils.error("Couldnt DC")
						dontDisconnect = true
						mc.addScheduledTask {
							//mc.world.sendQuittingDisconnectingPacket()
							//mc.loadWorld(null)
							//mc.world.sendQuittingDisconnectingPacket()
							//mc.loadWorld(null)
							
							//println("1")
							//println("2")
							mc.displayGuiScreen(GuiReconnecting(mainMenu, serverData))
							//mc.displayGuiScreen(GuiReconnecting(mainMenu, serverData))
							mc.setServerData(serverData)
							//println("3")
						}
					}
				}
			}
		}
	}
	
	val onGround by lazy {
		CPacketPlayer::class.java.declaredFields.first { it.name == "onGround" || it.name == "field_149474_g" }?.also {
			it.isAccessible = true
		} ?: error("Couldnt find field " + CPacketPlayer::class.java.declaredFields?.contentToString())
	}
	
	@Listener
	fun onPacket(event: PacketSendEvent) {
		//if (mode == "Packet") {
		val packet = event.packet
		if (packet is CPacketPlayer && mc.player.fallDistance >= 3f) {
			onGround.set(packet, true)
		}
		//}
	}
	
	/**
	 * @author dan
	 */
	private fun doesBBIntersect(entity: Entity, nextPosition: Vec3d): Boolean {
		val pos = entity.positionVector
		val bb = entity.entityBoundingBox
		val boxCorners = arrayOf(
			Vec3d(bb.minX, bb.minY, bb.minZ),
			Vec3d(bb.minX, bb.minY, bb.maxZ),
			Vec3d(bb.maxX, bb.minY, bb.minZ),
			Vec3d(bb.maxX, bb.minY, bb.maxZ)
		)
		for (corner in boxCorners) {
			val cornerDown = corner.subtract(pos).add(nextPosition)
			val result = entity.world.rayTraceBlocks(corner, cornerDown, true, false, true)
			if (result?.typeOfHit == RayTraceResult.Type.BLOCK) return true
		}
		return false
	}
}

class GuiReconnecting(private val previousGuiScreen: GuiScreen, private val serverData: ServerData): GuiScreen() {
	private var networkManager: NetworkManager
	
	init {
		println("4")
		super.mc = Minecraft.getMinecraft()
		println("5")
		val addr = ServerAddress.fromString(serverData.serverIP)
		println("6")
		this.connect(addr.ip, addr.port)
		println("7")
		networkManager = mc.connection!!.networkManager
		//(networkManager as MixinNetworkManager).dontDisconnect = true
	}
	
	private var cancel = false
	
	private fun connect(ip: String, port: Int) {
		println("Connecting to $ip, $port")
		thread(start = true, isDaemon = true, name = "Server Reconnector") {
			println("8")
			try {
				println("9")
				if (cancel) {
					return@thread
				}
				println("10")
				networkManager = NetworkManager.createNetworkManagerAndConnect(
					InetAddress.getByName(ip),
					port,
					mc.gameSettings.isUsingNativeTransport
				)
				networkManager.netHandler = NetHandlerLoginClient(networkManager, mc, previousGuiScreen)
				networkManager.sendPacket(C00Handshake(ip, port, EnumConnectionState.LOGIN, true))
				networkManager.sendPacket(CPacketLoginStart(mc.session.profile))
				println("11")
				mc.setServerData(serverData)
			} catch (hostException: UnknownHostException) {
				if (cancel) {
					return@thread
				}
				RuntimeException("Couldnt connect to server", hostException).printStackTrace()
				mc.displayGuiScreen(
					GuiDisconnected(
						previousGuiScreen,
						"connect.failed",
						TextComponentTranslation("disconnect.genericReason", "Unknown host")
					)
				)
			} catch (exception: Exception) {
				if (cancel) {
					return@thread
				}
				RuntimeException("Couldnt connect to server", exception).printStackTrace()
				mc.displayGuiScreen(
					GuiDisconnected(
						previousGuiScreen,
						"connect.failed",
						TextComponentTranslation("disconnect.genericReason", exception.toString())
					)
				)
			} finally {
				mc.setServerData(serverData)
			}
		}
	}
	
	override fun updateScreen() {
		networkManager.processReceivedPackets()
		if (networkManager.isChannelOpen) {
			NoFallModule.dontDisconnect = false
			println("Finished")
			mc.displayGuiScreen(null)
			mc.setServerData(serverData)
			mc.player.fallDistance = 0f
		}
		//} else {
		//networkManager.handleDisconnection()
		//}
	}
	
	override fun initGui() {
		this.buttonList.clear()
		this.buttonList.add(
			GuiButton(
				0,
				this.width / 2 - 100,
				this.height / 4 + 120 + 12,
				I18n.format("gui.cancel")
			)
		)
	}
	
	@Throws(IOException::class)
	override fun actionPerformed(button: GuiButton) {
		if (button.id == 0) {
			cancel = true
			networkManager.closeChannel(TextComponentString("Aborted"))
			mc.displayGuiScreen(previousGuiScreen)
		}
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
		//drawGradientRect(0, 0, width, height, setColourAlpha(-1072689136, 100), setColourAlpha(-804253680, 20))
		drawCenteredString(
			fontRenderer,
			I18n.format("connect.authorizing"),
			width / 2,
			height / 2 - 50,
			16777215
		)
		super.drawScreen(mouseX, mouseY, partialTicks)
	}
	
	override fun onGuiClosed() {
		NoFallModule.dontDisconnect = false
	}
	
	fun setColourAlpha(colour: Int, alpha: Int): Int {
		val r = (colour shr 24 and 255)
		val g = (colour shr 16 and 255)
		val b = (colour shr 8 and 255)
		val a = alpha//(colour and 255)
		val value =
			(a and 0xFF shl 24) or
			(r and 0xFF shl 16) or
			(g and 0xFF shl 8) or
			(b and 0xFF shl 0)
		return value
	}
}
