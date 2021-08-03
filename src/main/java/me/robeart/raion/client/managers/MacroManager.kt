package me.robeart.raion.client.managers

import me.robeart.raion.client.macro.Macro
import me.robeart.raion.client.util.Key

/**
 * @author cats
 */
object MacroManager: MutableMap<Key, Macro> {
	val macros = HashMap<Key, Macro>()
	override operator fun get(bind: Key): Macro? = macros[bind]
	operator fun set(bind: Key, macro: Macro) {
		macros[bind] = macro
	}
	
	override fun remove(bind: Key): Macro? = macros.remove(bind)
	override val size: Int = macros.size
	
	override fun containsKey(key: Key): Boolean = macros.containsKey(key)
	override fun containsValue(value: Macro): Boolean = macros.containsValue(value)
	override fun isEmpty(): Boolean = macros.isEmpty()
	override val entries: MutableSet<MutableMap.MutableEntry<Key, Macro>>
		get() = macros.entries
	override val keys: MutableSet<Key>
		get() = macros.keys
	override val values: MutableCollection<Macro>
		get() = macros.values
	
	override fun clear() = macros.clear()
	override fun put(key: Key, value: Macro): Macro? = macros.put(key, value)
	override fun putAll(from: Map<out Key, Macro>) = macros.putAll(from)
}
