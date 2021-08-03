package me.robeart.raion.client.command

import me.robeart.raion.client.util.ChatUtils

/**
 * @author cookiedragon234 09/Jun/2020
 */
object SeedCrackerCommand: Command("seedcracker", "Crack Seeds", ".seedcracker crack/find slime x,z, x,z x,z") {
	var threads = arrayListOf<CrackerThread>()
	
	override fun call(args: Array<out String>?) {
		/*if (args != null && args.isNotEmpty()) {
			if (args[0] == "status") {
				ChatUtils.message(buildString {
					print("${threads.size} threads:\n")
					print(threads.joinToString(separator = "\n", transform = { it.getProgress() }))
				})
				return
			} else if (args[0] == "stop") {
				threads.forEach {
					it.terminated = true
				}
				threads = arrayListOf()
			}
		}
		
		if (args == null || args.size < 3) {
			ChatUtils.error(this.usage)
			return
		}
		
		val control1 = args[0]
		val control2 = args[1]
		val args2 = args.toMutableList()
		args2.removeAt(0)
		args2.removeAt(1)
		
		fun parseCoords(coords: Collection<String>): MutableList<Vec2i> {
			return arrayListOf<Vec2i>().also {
				for (coord in coords) {
					try {
						val split = coord.split(',')
						it.add(Vec2i(split[0].toInt(), split[1].toInt()))
					} catch (t: Throwable) {
						println("Couldnt parse chunk coord $coord")
					}
				}
			}
		}
		
		when (control2) {
			"slime" -> {
				when (control1) {
					"crack" -> {
						val chunks: MutableList<Vec2i> = parseCoords(args2)
						
						if (chunks.isEmpty()) {
							ChatUtils.error("Please provide chunks")
							return
						}
						
						object: CrackerThread() {
							var posSeed = 0L
							var negSeed = -1L
							var pos = true
							
							override fun getProgress(): String = "Cracking slime chunk ($negSeed - $posSeed)"
							
							override fun run() {
								while (!terminated) {
									if (pos) {
										if (posSeed != -1L) {
											if (chunks.all { chunk ->
													WorldGenerationUtils.isSlimeChunk(posSeed, chunk.x, chunk.y)
												}) {
												ChatUtils.message("Found potential seed $posSeed")
											} else {
												println("Seed $posSeed")
											}
											
											if (posSeed == Long.MAX_VALUE) {
												posSeed = -1L
												ChatUtils.message("Reached max pos value")
											} else {
												posSeed += 1L
											}
										}
										pos = false
									} else {
										if (negSeed != 1L) {
											if (chunks.all { chunk ->
													WorldGenerationUtils.isSlimeChunk(negSeed, chunk.x, chunk.y)
												}) {
												ChatUtils.message("Found potential seed $negSeed")
											}
											
											if (posSeed == Long.MIN_VALUE) {
												posSeed = 1L
												ChatUtils.message("Reached max pos value")
											} else {
												posSeed -= 1L
											}
										}
										
										pos = true
									}
									
									if (posSeed == Long.MAX_VALUE && negSeed == Long.MIN_VALUE) {
										ChatUtils.message("Couldnt find seed")
										return
									}
								}
							}
						}.also {
							threads.add(it)
						}
						ChatUtils.message("Cracking...")
						return
					}
					"find" -> {
						val seed = args2.firstOrNull()?.toLongOrNull()
						if (seed == null) {
							ChatUtils.error("Please provide seed")
							return
						}
						
						object: CrackerThread() {
							var posChunk: Vec2i? = Vec2i(0, 0)
							var negChunk: Vec2i? = Vec2i(-1, -1)
							var pos = true
							
							override fun getProgress(): String = "Finding slime chunks ($negChunk - $posChunk)"
							
							override fun run() {
								while (!terminated) {
									if (pos) {
										if (posChunk != null) {
											if (WorldGenerationUtils.isSlimeChunk(seed, posChunk!!.x, posChunk!!.y)) {
												ChatUtils.message("Found potential chunk $posChunk")
											} else {
												println("Chunk $posChunk")
											}
											
											if (posChunk!!.x >= 1874999 || posChunk!!.y >= 1874999) {
												posChunk = null
												ChatUtils.message("Reached max pos value")
											} else {
												posChunk
											}
										}
										pos = false
									} else {
										if (negChunk != 1L) {
											if (chunks.all { chunk ->
													WorldGenerationUtils.isSlimeChunk(negChunk, chunk.x, chunk.y)
												}) {
												ChatUtils.message("Found potential seed $negChunk")
											}
											
											if (posChunk == Long.MIN_VALUE) {
												posChunk = 1L
												ChatUtils.message("Reached max pos value")
											} else {
												posChunk -= 1L
											}
										}
										
										pos = true
									}
									
									if (posChunk == Long.MAX_VALUE && negChunk == Long.MIN_VALUE) {
										ChatUtils.message("Couldnt find seed")
										return
									}
								}
							}
						}.also {
							threads.add(it)
						}
						ChatUtils.message("Cracking...")
						return
					}
				}
			}
		}*/
		ChatUtils.error(this.usage)
		return
	}
	
	abstract class CrackerThread: Thread() {
		var terminated = false
		abstract fun getProgress(): String
	}
}
