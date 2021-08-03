package me.robeart.raion.client.module.combat.cookiecaura

import me.robeart.raion.client.module.Module

/**
 * @author cookiedragon234 10/Jun/2020
 */
object CookieCrystalAura: Module("AutoCrystal", "", Category.COMBAT) {
	/*private val placeDistance by ValueDelegate(FloatValue("Place Distance", 4.4f, 0f, 6f, 0.2f))
	private val placeYDistance by ValueDelegate(FloatValue("Place Y Distance", 3f, 0f, 6f, 0.2f))
	private val placeThroughWallsSetting = BooleanValue("Walls", true)
	private val placeThroughWalls by ValueDelegate(placeThroughWallsSetting)
	private val throughWallsDistance by ValueDelegate(FloatValue("Place Distance", 4.4f, 0f, 6f, 0.2f, placeThroughWallsSetting))
	
	override fun moduleLogic() {
	}
	
	fun searchForPlacePositions() {
		val negDistance = floor(placeDistance).toInt()
		val posDistance = ceil(placeDistance).toInt()
		val negYDistance = floor(placeYDistance).toInt()
		val posYDistance = ceil(placeYDistance).toInt()
		
		val distanceSq = placeDistance.toDouble().pow(2)
		
		val currentPos = PooledMutableBlockPos.retain()
		val down = PooledMutableBlockPos.retain()
		for (x in negDistance..posDistance) {
			for (y in negYDistance..posYDistance) {
				for (z in negDistance..posDistance) {
					currentPos.setPos(x, y, z)
					canPlaceAt(currentPos, down, distanceSq)
				}
			}
		}
		currentPos.release()
		down.release()
	}
	
	private fun canPlaceAt(position: MutableBlockPos, down: MutableBlockPos, distanceSq: Double): Boolean {
		if (mc.player.getDistanceSq(position.x + 0.5, position.y + 0.5, position.z + 0.5) <= distanceSq) {
			val state = mc.world.getBlockState(position)
			
			if (state.material == Material.AIR) {
				down.setPos(position.x, position.y - 1, position.z)
				val downState = mc.world.getBlockState(down)
				
				if (downState.block == Blocks.OBSIDIAN || downState.block == Blocks.BEDROCK) {
				}
			}
		}
		return false
	}
	data class PlacePosition(
		val pos: BlockPos,
		val damage: MutableMap<Entity, Double>
	)*/
}
