package me.robeart.raion.client.module.misc

import me.robeart.raion.client.events.events.client.ClickMouseButtonChatEvent
import me.robeart.raion.client.module.Module
import net.minecraft.network.play.client.CPacketChatMessage
import org.lwjgl.input.Mouse.getX
import org.lwjgl.input.Mouse.getY
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener

/**
 * @author Robeart
 */
object MiddleClickIgnore: Module("ClickIgnore", "MiddleClick chat message to ignore the sender", Category.MISC) {
	
	private var down = false
	
	@Listener
	fun onMouseClick(event: ClickMouseButtonChatEvent) {
		if (event.mouseButton == 2) {
			val t = mc.ingameGUI.chatGUI.getChatComponent(getX(), getY()) ?: return
			val name = t.formattedText.split(" ")[0].replace("[<>]".toRegex(), "")
			println(name)
			val playerinfo = mc.player.connection.getPlayerInfo(name) ?: return
			if (playerinfo.gameProfile != mc.player.gameProfile)
				mc.connection?.sendPacket(CPacketChatMessage("/ignore $name"))
		}
	}
}
