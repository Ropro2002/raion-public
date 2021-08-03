package me.cookiedragon234.falcon.antidump.antis

/**
 * @author cookiedragon234 03/Mar/2020
 */
object Anti0x22: AntiPaster {
	override fun pasterDown(name: String) {
		if (name.equals("0x22", true)) {
			AntiPaster.eleminateThePaster()
		}
	}
}
