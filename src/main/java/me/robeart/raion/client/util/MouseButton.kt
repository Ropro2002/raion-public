package me.robeart.raion.client.util

enum class MouseButton {
	LEFT,
	MIDDLE,
	RIGHT;
	
	companion object {
		fun fromState(buttonState: Int): MouseButton {
			return when (buttonState) {
				0    -> LEFT
				1    -> RIGHT
				2    -> MIDDLE
				else -> error("Illegal state $buttonState")
			}
		}
	}
}
