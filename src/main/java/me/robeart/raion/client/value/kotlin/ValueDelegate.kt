package me.robeart.raion.client.value.kotlin

import me.robeart.raion.client.value.Value
import kotlin.reflect.KProperty

/**
 * @author cookiedragon234 27/Mar/2020
 */
class ValueDelegate<F: Any, out T: Value<F>>(val value: T) {
	fun getActualValue(): T = value
	
	operator fun getValue(thisRef: Any?, property: KProperty<*>): F {
		return value.value
	}
	
	operator fun setValue(thisRef: Any?, property: KProperty<*>, newVal: F) {
		value.value = newVal
	}
}
