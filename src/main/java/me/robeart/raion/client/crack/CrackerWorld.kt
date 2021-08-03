package me.robeart.raion.client.crack

import net.minecraft.client.multiplayer.ChunkProviderClient
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.profiler.Profiler
import net.minecraft.world.World
import net.minecraft.world.WorldProvider
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.chunk.storage.IChunkLoader
import net.minecraft.world.gen.structure.template.TemplateManager
import net.minecraft.world.storage.IPlayerFileData
import net.minecraft.world.storage.ISaveHandler
import net.minecraft.world.storage.WorldInfo
import java.io.File

/**
 * @author cookiedragon234 13/Jun/2020
 */
class CrackerWorld(val fakeSeed: Long, info: WorldInfo, providerIn: WorldProvider): World(CrackerSaveHandler(), info, providerIn, Profiler(), false) {
	override fun createChunkProvider(): IChunkProvider {
		return ChunkProviderClient(this)
	}
	
	override fun isChunkLoaded(x: Int, z: Int, allowEmpty: Boolean): Boolean {
		TODO("Not yet implemented")
	}
}

class CrackerSaveHandler(): ISaveHandler {
	override fun checkSessionLock() {
		TODO("Not yet implemented")
	}
	
	override fun getPlayerNBTManager(): IPlayerFileData {
		TODO("Not yet implemented")
	}
	
	override fun saveWorldInfoWithPlayer(worldInformation: WorldInfo, tagCompound: NBTTagCompound) {
		TODO("Not yet implemented")
	}
	
	override fun getMapFileFromName(mapName: String): File {
		TODO("Not yet implemented")
	}
	
	override fun flush() {
		TODO("Not yet implemented")
	}
	
	override fun loadWorldInfo(): WorldInfo? {
		TODO("Not yet implemented")
	}
	
	override fun getWorldDirectory(): File {
		TODO("Not yet implemented")
	}
	
	override fun getStructureTemplateManager(): TemplateManager {
		TODO("Not yet implemented")
	}
	
	override fun saveWorldInfo(worldInformation: WorldInfo) {
		TODO("Not yet implemented")
	}
	
	override fun getChunkLoader(provider: WorldProvider): IChunkLoader {
		TODO("Not yet implemented")
	}
}
