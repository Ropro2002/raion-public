package me.robeart.raion.client.crack

import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import net.minecraft.init.Biomes
import net.minecraft.world.WorldType
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.ChunkGeneratorSettings
import net.minecraft.world.gen.layer.GenLayer
import net.minecraft.world.gen.layer.IntCache

/**
 * @author cookiedragon234 13/Jun/2020
 */
object BiomeGenerator {
	private val genLayers = HashMap<Long, Array<GenLayer>>()
	
	fun getLayersForSeed(seed: Long): Array<GenLayer> {
		var out = genLayers[seed]
		
		if (out == null) {
			out = GenLayer.initializeAllBiomeGenerators(seed, WorldType.DEFAULT, ChunkGeneratorSettings.Factory().build())
			genLayers[seed] = out
		}
		
		return out!!
	}
	
	fun getBiomesAtLocation(seed: Long, x: Int, y: Int, width: Int, height: Int): Array<Biome> {
		val genLayers = getLayersForSeed(seed)
		val biomeGen = genLayers[0] // Quarter resolution biome generator
		val biomeGenFull = genLayers[1] // Full resolution biome generator, needed for accurate biome calculation
		
		IntCache.resetIntCache()
		val biomeData = biomeGenFull.getInts(x, y, width, height)
		return Array(width * height) { i ->
			Biome.getBiome(biomeData[i], Biomes.PLAINS)
		}
	}
}
