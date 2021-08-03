package me.cookiedragon234.falcon.antidump.antis

/**
 * @author cookiedragon234 03/Mar/2020
 */
object AntiBella: AntiPaster {
	override fun pasterDown(name: String) {
		if (name.equals("Bella", true)) {
			AntiPaster.eleminateThePaster()
		}
	}
}
