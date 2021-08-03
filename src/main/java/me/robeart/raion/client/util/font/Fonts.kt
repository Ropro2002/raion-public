package me.robeart.raion.client.util.font

import me.robeart.raion.client.Raion

/**
 * @author Robeart 1/07/2020
 */
object Fonts {

    val font = Raion.INSTANCE.getFont();

    val font20 = MinecraftFontRenderer(font.deriveFont(20f), true)
    val font28 = MinecraftFontRenderer(font.deriveFont(28f), true)
    val font35 = MinecraftFontRenderer(font.deriveFont(35f), true)
    val font36 = MinecraftFontRenderer(font.deriveFont(36f), true)
    val font40 = MinecraftFontRenderer(font.deriveFont(40f), true)
    val font42 = MinecraftFontRenderer(font.deriveFont(42f), true)
    val font48 = MinecraftFontRenderer(font.deriveFont(48f), true)
    val font80 = MinecraftFontRenderer(font.deriveFont(80f), true)


}