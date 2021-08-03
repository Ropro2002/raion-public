package me.robeart.raion.client.module.combat

import me.robeart.raion.client.events.events.network.PacketReceiveEvent
import me.robeart.raion.client.events.events.player.UpdateWalkingPlayerEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.minecraft.MinecraftUtils
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.block.BlockObsidian
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.network.play.server.SPacketBlockChange
import net.minecraft.network.play.server.SPacketTimeUpdate
import net.minecraft.util.math.BlockPos
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener

/**
 * @author cookiedragon234 02/Jun/2020
 */
object InstantBreakCrystal: Module("InstaCrystal", "Place a crystal after breaking a block", Category.COMBAT) {
	val onlyObby by ValueDelegate(BooleanValue("Only Obsidian", true))
	val fast by ValueDelegate(BooleanValue("Fast", false))

	var attackBlockLast: BlockPos? = null
	
	@Listener
	fun onBlockClick(event: PlayerInteractEvent.LeftClickBlock) {
		val state = mc.world.getBlockState(event.pos)
		if (!onlyObby || (state.block == Blocks.OBSIDIAN || state.block is BlockObsidian)) {
			attackBlockLast = event.pos
		}
	}

	@Listener
	fun onUpdate(event: UpdateWalkingPlayerEvent) {
		if (fast && placeNext != null) {
			if (MinecraftUtils.holdItem(Items.END_CRYSTAL)) {
				if (MinecraftUtils.place(placeNext)) {
					placeNext = null
				}
			}
		}
	}

	var placeNext: BlockPos? = null

	@Listener
	fun onPacketR(event: PacketReceiveEvent) {
		if (mc.player == null || mc.player.inventory == null || mc.world == null || mc.connection == null) return

		val previousSlot = mc.player.inventory.currentItem
		try {
			val packet = event.packet
			if (packet is SPacketBlockChange) {
				val block = packet.blockPosition
				if (attackBlockLast == block) {
					if (packet.blockState.block == Blocks.AIR) {
						placeNext = block
						attackBlockLast = null
						if (MinecraftUtils.holdItem(Items.END_CRYSTAL)) {
							if (MinecraftUtils.place(placeNext)) {
							}
						}
					}
				}
				if (block == placeNext) {
					if (packet.blockState.block != Blocks.AIR) {
						placeNext = null
					}
				}
			} else if (packet is SPacketTimeUpdate) {
				if (!fast && placeNext != null) {
					if (MinecraftUtils.holdItem(Items.END_CRYSTAL)) {
						if (MinecraftUtils.place(placeNext)) {
						}
					}
				}
			}
		} finally {
			mc.player.inventory.currentItem = previousSlot
		}
	}
}
