package me.robeart.raion.client.crack

import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import java.util.Random

/**
 * @author cookiedragon234 13/Jun/2020
 */
interface StructureGenerator {
	fun generateStructure(randomIn: Random, chunkCoord: ChunkPos)
}
