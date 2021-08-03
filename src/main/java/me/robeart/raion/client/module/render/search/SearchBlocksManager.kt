package me.robeart.raion.client.module.render.search

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

/**
 * @author cookiedragon234 19/Jun/2020
 */
object SearchBlocksManager {
	var highlightedBlocks: MutableCollection<BlockPos> = HashSet()
	val toHighlight = hashSetOf<Block>()
	
	fun shouldHighlight(blockState: IBlockState?): Boolean {
		if (blockState == null) return false
		return toHighlight.contains(blockState.block)
	}
	
	fun getToHighlighted() = toHighlight.map { it.registryName?.path ?: it.localizedName }
	
	fun clearHighlighted(): Int {
		return toHighlight.size.also {
			toHighlight.clear()
		}
	}
	
	private val shulkers by lazy {
		arrayOf(
			Blocks.WHITE_SHULKER_BOX,
			Blocks.ORANGE_SHULKER_BOX,
			Blocks.MAGENTA_SHULKER_BOX,
			Blocks.LIGHT_BLUE_SHULKER_BOX,
			Blocks.YELLOW_SHULKER_BOX,
			Blocks.LIME_SHULKER_BOX,
			Blocks.PINK_SHULKER_BOX,
			Blocks.GRAY_SHULKER_BOX,
			Blocks.SILVER_SHULKER_BOX,
			Blocks.CYAN_SHULKER_BOX,
			Blocks.PURPLE_SHULKER_BOX,
			Blocks.BLUE_SHULKER_BOX,
			Blocks.BROWN_SHULKER_BOX,
			Blocks.GREEN_SHULKER_BOX,
			Blocks.RED_SHULKER_BOX,
			Blocks.BLACK_SHULKER_BOX
		)
	}
	
	fun addHighlight(name: String, highlight: Boolean): Boolean {
		if (name == "shulker") {
			shulkers.forEach {
				addHighlight(
					it,
					highlight
				)
			}
		} else {
			val block = Block.getBlockFromName(name.toLowerCase()) ?: return false
			addHighlight(block, highlight)
		}
		return true
	}
	
	fun addHighlight(b: Block, highlight: Boolean) {
		if (highlight) {
			toHighlight.add(b)
		} else {
			toHighlight.remove(b)
		}
	}
}
