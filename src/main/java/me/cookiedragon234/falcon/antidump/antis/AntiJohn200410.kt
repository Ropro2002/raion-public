package me.cookiedragon234.falcon.antidump.antis

/**
 * @author cookiedragon234 03/Mar/2020
 */
object AntiJohn200410: AntiPaster {
	override fun pasterDown(name: String) {
		if (name.equals("john200410", true)) {
			AntiPaster.eleminateThePaster()
		}
	}
}
