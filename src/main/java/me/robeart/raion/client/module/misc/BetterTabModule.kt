package me.robeart.raion.client.module.misc

import me.robeart.raion.client.Raion
import me.robeart.raion.client.events.events.network.PacketReceiveEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.module.Module.Category.MISC
import me.robeart.raion.client.util.MathUtils
import me.robeart.raion.client.util.plus
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.network.play.server.SPacketPlayerListItem
import net.minecraft.network.play.server.SPacketPlayerListItem.Action.ADD_PLAYER
import net.minecraft.network.play.server.SPacketPlayerListItem.Action.REMOVE_PLAYER
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.util.text.TextFormatting
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener
import javax.swing.text.html.HTML.Tag.PRE


/**
 * @author cookiedragon234 03/Mar/2020
 */
object BetterTabModule: Module("BetterTab", "Improves the tab menu", MISC) {
	val showPing by ValueDelegate(BooleanValue("Show Ping", true))
	val colourPing by ValueDelegate(BooleanValue("Colour Ping", true))
	val performance by ValueDelegate(BooleanValue("Better Performance", true))
	val extraTab by ValueDelegate(BooleanValue("Extra Slots", true))
	val highlightFriends by ValueDelegate(BooleanValue("Highlight Friends", true))

	var cachedList: MutableList<NetworkPlayerInfo>? = null
		private set

	fun sort(list: MutableList<NetworkPlayerInfo>): MutableList<NetworkPlayerInfo> {
		list.sortWith(Comparator { networkPlayerInfo, networkPlayerInfo2 ->
			java.lang.Boolean.compare(
				Raion.INSTANCE.friendManager.isFriend(networkPlayerInfo2.gameProfile),
				Raion.INSTANCE.friendManager.isFriend(networkPlayerInfo.gameProfile)
			)
		})
		list.sortWith(Comparator { networkPlayerInfo, networkPlayerInfo2 ->
			java.lang.Boolean.compare(
				Raion.INSTANCE.friendManager.isDeveloper(networkPlayerInfo2.gameProfile.name),
				Raion.INSTANCE.friendManager.isDeveloper(networkPlayerInfo.gameProfile.name)
			)
		})
		return list
	}

	fun markPlayerListDirty() {
		cachedList = null
	}

	@Listener
	fun onPacket(event: PacketReceiveEvent) {
		if (event.stage != PRE) return

		val packet = event.packet
		if (packet is SPacketPlayerListItem) {
			if (packet.action == REMOVE_PLAYER || packet.action == ADD_PLAYER) {
				markPlayerListDirty()
			}
		}
	}

	fun drawPing(x: Int, xOffset: Int, y: Int, networkPlayerInfoIn: NetworkPlayerInfo): Boolean {
		if (state && showPing) {
			val ping = networkPlayerInfoIn.responseTime
			val colour = if (colourPing) MathUtils.getBlendedColor(
				-(MathUtils.clamp(
					ping / 150f,
					0f,
					1f
				) - 1)
			).rgb else -1
			GlStateManager.translate(0f, 0f, 1000f)
			val width = mc.fontRenderer.getStringWidth(ping.toString())
			mc.fontRenderer.drawString(ping.toString(), x + xOffset - width, y, colour)
			GlStateManager.translate(0f, 0f, -1000f)
			return true
		}
		return false
	}

	fun getPlayerName(networkPlayerInfoIn: NetworkPlayerInfo): String {
		var name = networkPlayerInfoIn.displayName?.formattedText
			?: ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.playerTeam, networkPlayerInfoIn.gameProfile.name)
		if (this.state && this.highlightFriends) {
			if (Raion.INSTANCE.friendManager.isFriend(networkPlayerInfoIn.gameProfile)) {
				name = TextFormatting.GREEN + name + TextFormatting.RESET
			}
		}
		if (Raion.INSTANCE.friendManager.isDeveloper(networkPlayerInfoIn.gameProfile.name)) {
			name = TextFormatting.GOLD + TextFormatting.BOLD + name + TextFormatting.RESET
		}
		return name
	}
}
