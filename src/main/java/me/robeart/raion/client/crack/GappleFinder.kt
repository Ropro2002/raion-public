package me.robeart.raion.client.crack

import me.robeart.raion.client.crack.BiomeGenerator.getBiomesAtLocation
import net.minecraft.init.Biomes
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.WorldType
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.ChunkGeneratorSettings
import net.minecraft.world.gen.layer.GenLayer
import net.minecraft.world.gen.layer.IntCache
import java.util.Random

/**
 * @author cookiedragon234 13/Jun/2020
 */
@ExperimentalStdlibApi
fun find() {
	var currentPos = ChunkPos(0,0)
	var nextChange = EnumFacing.NORTH
	val seed: Long = 12
	
	fun getGaps(seed: Long, chunkPos: ChunkPos): List<Vec3d> {
		return buildList {
			val x = chunkPos.x // Chunk Pos X
			val z = chunkPos.z // Chunk Pos Z
			val i = chunkPos.xStart // Block Pos X
			val j = chunkPos.zStart // Block Pos Z
			val blockPos = BlockPos(i, 0, j)
			val biome: Biome = getBiomesAtLocation(seed, i + 16, j + 16, 1, 1)[0]
			val rand = Random()
			val k: Long = rand.nextLong() / 2L * 2L + 1L
			val l: Long = rand.nextLong() / 2L * 2L + 1L
			rand.setSeed(x.toLong() * k + z.toLong() * l xor seed)
			
			// Generate Mineshafts
			MineshaftGenerator.generateStructure(rand, chunkPos)
		}
	}
	
	while (true) {
		// calc
		
		
		when (nextChange) {
			EnumFacing.NORTH -> {
				currentPos.x + 1
				currentPos = ChunkPos(currentPos.x + 1, currentPos.z)
				nextChange = EnumFacing.EAST
			}
			EnumFacing.EAST -> {
				currentPos = ChunkPos(currentPos.x, currentPos.z + 1)
				nextChange = EnumFacing.SOUTH
			}
			EnumFacing.SOUTH -> {
				currentPos = ChunkPos(currentPos.x - 1, currentPos.z)
				nextChange = EnumFacing.WEST
			}
			EnumFacing.WEST -> {
				currentPos = ChunkPos(currentPos.x, currentPos.z - 1)
				nextChange = EnumFacing.NORTH
			}
		}
	}
}
