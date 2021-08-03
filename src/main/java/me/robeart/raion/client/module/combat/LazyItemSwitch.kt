package me.robeart.raion.client.module.combat

import me.robeart.raion.client.module.Module
import net.minecraft.client.multiplayer.PlayerControllerMP

/**
 * @author cookiedragon234 25/Jul/2020
 */
object LazyItemSwitch: Module("LazyItemSwitch", "Spoof your server side item until necessary", Category.COMBAT) {
	fun updatePlayerControllerOnTick(playerController: PlayerControllerMP) {
		if (!this.state) {
			playerController.updateController()
		} else {
			// Send/receive packets without syncing item as would normally be done each tick
			
			val conn = mc.player.connection
			if (conn.networkManager.isChannelOpen) {
				conn.networkManager.processReceivedPackets()
			} else {
				conn.networkManager.handleDisconnection()
			}
		}
	}
}
