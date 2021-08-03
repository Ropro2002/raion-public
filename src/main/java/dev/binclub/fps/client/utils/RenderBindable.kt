package dev.binclub.fps.client.utils

/**
 * @author cookiedragon234 05/Jul/2020
 */
interface RenderBindable {
	fun bind()
	fun unbind()
}

inline fun <T> RenderBindable.use(block: () -> T): T {
	try {
		this.bind()
		return block()
	} finally {
		this.unbind()
	}
}
