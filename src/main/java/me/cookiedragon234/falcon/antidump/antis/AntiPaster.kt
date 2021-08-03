package me.cookiedragon234.falcon.antidump.antis

import me.cookiedragon234.falcon.NativeAccessor

/**
 * @author cookiedragon234 03/Mar/2020
 */
interface AntiPaster {
	fun pasterDown(name: String)
	
	companion object {
		inline fun eleminateThePaster(): Nothing {
			NativeAccessor.prepareTransformer(null, null, null, null)
			throw NullPointerException()
			//for (i in 0 until 50) {
			//	println("Paster Down! ♪┏(・o･)┛♪┗ ( ･o･) ┓♪")
			//}
			//CookieFuckery.shutdownHard()
		}
	}
}
