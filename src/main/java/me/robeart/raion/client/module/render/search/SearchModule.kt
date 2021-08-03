package me.robeart.raion.client.module.render.search

import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import me.robeart.raion.client.events.EventStageable
import me.robeart.raion.client.events.events.network.PacketReceiveEvent
import me.robeart.raion.client.events.events.render.Render3DEvent
import me.robeart.raion.client.events.events.world.ChunkEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.module.render.search.SearchBlocksManager.highlightedBlocks
import me.robeart.raion.client.module.render.search.SearchBlocksManager.shouldHighlight
import me.robeart.raion.client.util.Configurable
import me.robeart.raion.client.util.immutable
import me.robeart.raion.client.util.mutableBlockPos
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.FloatValue
import me.robeart.raion.client.value.IntValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.client.multiplayer.ChunkProviderClient
import net.minecraft.network.play.server.SPacketBlockChange
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.storage.ExtendedBlockStorage
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.FMLNetworkEvent
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener

/**
 * @author cookiedragon234 24/Apr/2020
 */
object SearchModule: Module("Search", "Highlight blocks", Category.RENDER), Configurable {
	val maxDistance by ValueDelegate(IntValue("Max Distance", 100, 50, 500, 10))
	val maxBlocks by ValueDelegate(IntValue("Max Blocks", 100, 100, 500, 10))
	
	val highlight = BooleanValue("Highlight", true)
	val hWidth by ValueDelegate(FloatValue("hWidth", 2f, 0f, 5f, 0.1f, highlight))
	val hRed by ValueDelegate(IntValue("hRed", 200, 0, 255, 1, highlight))
	val hGreen by ValueDelegate(IntValue("hGreen", 0, 0, 255, 1, highlight))
	val hBlue by ValueDelegate(IntValue("hBlue", 0, 0, 255, 1, highlight))
	val hAlpha by ValueDelegate(IntValue("hAlpha", 100, 0, 255, 1, highlight))
	
	val trace = BooleanValue("Trace", true)
	val tWidth by ValueDelegate(FloatValue("tWidth", 2f, 0f, 5f, 0.1f, trace))
	val tRed by ValueDelegate(IntValue("tRed", 200, 0, 255, 1, trace))
	val tGreen by ValueDelegate(IntValue("tGreen", 0, 0, 255, 1, trace))
	val tBlue by ValueDelegate(IntValue("tBlue", 0, 0, 255, 1, trace))
	val tAlpha by ValueDelegate(IntValue("tAlpha", 100, 0, 255, 1, trace))
	
	val loadedChunksField by lazy {
		try {
			ChunkProviderClient::class.java.declaredFields.firstOrNull { it.type == Long2ObjectMap::class.java }?.also { it.isAccessible = true }
		} catch (t: Throwable) { t.printStackTrace(); null }
	}
	
	override fun onEnable() {
		highlightedBlocks.clear()
		loadedChunksField?.let { field ->
			field.get(mc.world?.chunkProvider ?: return)?.let { loadedChunks ->
				loadedChunks as Long2ObjectMap<Chunk>
				for (chunk in loadedChunks.values) {
					if (chunk != null) {
						try {
							searchChunk(chunk)
						} catch (t: Throwable) {
							t.printStackTrace()
						}
					}
				}
			}
		}
	}
	override fun onDisable() { highlightedBlocks.clear() }
	@Listener
	private fun onDisconnect(event: FMLNetworkEvent.ClientDisconnectionFromServerEvent) { highlightedBlocks.clear() }
	
	@Listener
	private fun onChunkUnload(event: net.minecraftforge.event.world.ChunkEvent.Unload) {
		if (!event.chunk.isLoaded) return
		
		val chunkX = event.chunk.x
		val chunkZ = event.chunk.z
		
		highlightedBlocks.removeIf { block -> (block.x / 16) == chunkX || (block.z / 16) == chunkZ }
	}
	
	private val yBase by lazy {
		try {
			ExtendedBlockStorage::class.java.declaredFields.first { it.name == "field_76684_a" || it.name == "yBase" }
				?.also {
					it.isAccessible = true
				} ?: error("Couldnt find field ${ExtendedBlockStorage::class.java.declaredFields.contentToString()}")
		} catch (t: Throwable) {
			t.printStackTrace()
			FMLCommonHandler.instance().exitJava(0, false)
			throw t
		}
	}
	
	fun <E> MutableCollection<E>.removeFirst(n: Int): MutableCollection<E> {
		if (n >= 1 && n <= size) {
			var size = this.size
			val it = this.iterator()
			while (n >= 1 && n <= size) {
				it.remove()
				size -= 1
			}
		}
		return this
	}
	
	@Listener
	fun onChunk(event: ChunkEvent) {
		try {
			searchChunk(event.chunk)
		} catch (t: Throwable) {
			t.printStackTrace()
		}
	}
	
	fun searchChunk(chunk: Chunk?) {
		if (chunk == null) return
		
		if (highlightedBlocks.size > maxBlocks) {
			highlightedBlocks.removeFirst(highlightedBlocks.size - maxBlocks)
		}
		
		val chunkX = chunk.x
		val chunkZ = chunk.z
		
		val realChunkX = chunkX * 16
		val realChunkZ = chunkZ * 16
		
		//highlightedBlocks.removeIf { block -> ((block.x.toInt() / 16) == chunkX || (block.z.toInt() / 16) == chunkZ)}
		
		for (storage in chunk.blockStorageArray) {
			if (storage == null) {
				continue
			}
			val ybase = (yBase.get(storage) as Int)
			mutableBlockPos { mutableBlockPos ->
				for (x in 0..15) {
					for (y in 0..15) {
						for (z in 0..15) {
							mutableBlockPos.setPos(realChunkX + x, ybase + y, realChunkZ + z)
							if (shouldHighlight(storage[x, y, z])) {
								highlightedBlocks.add(mutableBlockPos.immutable())
							} else {
								highlightedBlocks.remove(mutableBlockPos)
							}
						}
					}
				}
			}
		}
		if (highlightedBlocks.size > maxBlocks) {
			highlightedBlocks.removeFirst(highlightedBlocks.size - maxBlocks)
		}
	}
	
	@Listener
	fun onPacket(event: PacketReceiveEvent) {
		if (event.stage != EventStageable.EventStage.POST) return
		
		try {
			when (val packet = event.packet) {
				is SPacketBlockChange -> {
					if (shouldHighlight(packet.blockState)) {
						if (highlightedBlocks.size > maxBlocks) {
							highlightedBlocks.removeFirst(highlightedBlocks.size - maxBlocks)
						}
						highlightedBlocks.add(packet.blockPosition)
					} else {
						highlightedBlocks.remove(packet.blockPosition)
					}
				}
			}
		} catch (t: Throwable) {
			t.printStackTrace()
		}
	}
	
	@Listener
	private fun onRender3D(event: Render3DEvent) = SearchRenderer.onRender3D(event)
	
	override fun load() = SearchConfiguration.load()
	override fun save() = SearchConfiguration.save()
}
