package me.cookiedragon234.falcon.antidump.antis

/**
 * @author cookiedragon234 03/Mar/2020
 */
object AntiBabbaj: AntiPaster {
	override fun pasterDown(name: String) {
		if (name.equals("Babbaj", true)) {
			AntiPaster.eleminateThePaster()
		}
	}
}
