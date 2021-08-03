package me.robeart.raion.client.util.font

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureUtil
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage

/**
 * @author cookiedragon234 12/Jun/2020
 */
class BitmapFontRenderer(val font: Font, startChar: Int = 31, stopChar: Int = 255) {
	private var fontHeight = -1
	private val charLocations = arrayOfNulls<CharLocation>(stopChar)

	private val colorCode = IntArray(32)
	private val colorcodeIdentifiers = "0123456789abcdefklmnor"
	
	private var textureID = 0
	private var textureWidth = 0
	private var textureHeight = 0
	
	val height: Int
		get() = (fontHeight - 8) / 2
	
	init {
		for (index in 0..31) {
			val noClue = (index shr 3 and 1) * 85
			var red = (index shr 2 and 1) * 170 + noClue
			var green = (index shr 1 and 1) * 170 + noClue
			var blue = (index and 1) * 170 + noClue
			if (index == 6) {
				red += 85
			}
			if (index >= 16) {
				red /= 4
				green /= 4
				blue /= 4
			}
			colorCode.set(index, red and 255 shl 16 or (green and 255 shl 8) or (blue and 255))
		}
		renderBitmap(startChar, stopChar)
	}
	
	fun isSupported(str: String): Boolean = str.all { isSupported(it) }
	fun isSupported(char: Char): Boolean = char.toInt() < charLocations.size
	
	val mcFallbackDrawer: (char: Char, currX: Double, scale: Float, y :Double, color: Int) -> Int = { char, currX, scale, y, color ->
		Minecraft.getMinecraft().fontRenderer.drawString("$char", currX.toFloat() * scale + 1, (y * 2F).toFloat() + 1, color, false)
		Minecraft.getMinecraft().fontRenderer.getStringWidth("$char")
	}
	
	/**
	 * Allows you to draw a string with the target font
	 *
	 * @param text  to render
	 * @param x     location for target position
	 * @param y     location for target position
	 * @param color of the text
	 */
	@JvmOverloads
	fun drawString(text: String, x: Double, y: Double, color: Int, shadow: Boolean, fallback: (char: Char, currX: Double, scale: Float, y :Double, color: Int) -> Int = mcFallbackDrawer): Float {
		val scale = 0.25f
		val reverse = 1 / scale
		
		GlStateManager.pushMatrix()
		GlStateManager.enableAlpha()
		GlStateManager.enableBlend()
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
		
		GlStateManager.scale(scale, scale, scale)
		GlStateManager.bindTexture(textureID)
		
		val red: Float = (color shr 16 and 0xff) / 255F
		val green: Float = (color shr 8 and 0xff) / 255F
		val blue: Float = (color and 0xff) / 255F
		val alpha: Float = (color shr 24 and 0xff) / 255F
		
		GlStateManager.color(red, green, blue, alpha)
		
		var currX = x * 4f
		var skip = false
		var i = 0
		for(char in text.toCharArray()) {
			if(skip) {
				skip = false
				continue
			}
			if (char == '\u00a7' && i + 1 < text.length) {
				var colorIndex = colorcodeIdentifiers.indexOf(text[i + 1])
				if (colorIndex < 16) { // coloring
					if (colorIndex < 0 || colorIndex > 15) {
						colorIndex = 15
					}
					if (shadow) {
						colorIndex += 16
					}
					val colorcode: Int = colorCode.get(colorIndex)
					GlStateManager.color((colorcode shr 16 and 255).toFloat() / 255.0f, (colorcode shr 8 and 255).toFloat() / 255.0f, (colorcode and 255).toFloat() / 255.0f, alpha)
				} else if (colorIndex == 21) { // reset
					GlStateManager.color(red, green, blue, alpha)
				}
				skip = true
			} else {
				if (char.toInt() >= charLocations.size) {
					GlStateManager.scale(reverse, reverse, reverse)
					currX += (mcFallbackDrawer(char, currX, scale, (y), color)) * reverse
					GlStateManager.scale(scale, scale, scale)
					GlStateManager.bindTexture(textureID)
					GlStateManager.color(red, green, blue, alpha)
				} else {
					val fontChar = charLocations[char.toInt()] ?: continue

					drawChar(fontChar, currX.toFloat(), (y * 4f).toFloat())
					currX += fontChar.width - 8.0
				}
			}
			i++
		}
		GlStateManager.scale(reverse, reverse, reverse)
		GlStateManager.popMatrix()
		return currX.toFloat()
	}
	
	/**
	 * Draw char from texture to display
	 *
	 * @param char target font char to render
	 * @param x        target positon x to render
	 * @param y        target potion y to render
	 */
	private fun drawChar(char: CharLocation, x: Float, y: Float) {
		val width = char.width.toFloat()
		val height = char.height.toFloat()
		val srcX = char.x.toFloat()
		val srcY = char.y.toFloat()
		val renderX = srcX / textureWidth
		val renderY = srcY / textureHeight
		val renderWidth = width / textureWidth
		val renderHeight = height / textureHeight
		
		if (true) {
			FontUtils.drawTextureRect(x, y, width, height, renderX, renderY, renderX + renderWidth, renderY + renderHeight)
		} else {
			GL11.glBegin(GL11.GL_QUADS)
			GL11.glTexCoord2f(renderX, renderY)
			GL11.glVertex2f(x * .1f, y * .1f)
			GL11.glTexCoord2f(renderX, renderY + renderHeight)
			GL11.glVertex2f(x * .1f, (y + height) * .1f)
			GL11.glTexCoord2f(renderX + renderWidth, renderY + renderHeight);
			GL11.glVertex2f((x + width) * .1f, (y + height) * .1f)
			GL11.glTexCoord2f(renderX + renderWidth, renderY)
			GL11.glVertex2f((x + width), y)
			GL11.glEnd()
		}
	}
	
	/**
	 * Render font chars to a bitmap
	 */
	private fun renderBitmap(startChar: Int, stopChar: Int) {
		val fontImages = arrayOfNulls<BufferedImage>(stopChar)
		var rowHeight = 0
		var charX = 0
		var charY = 0
		
		for (targetChar in startChar until stopChar) {
			if(targetChar == 127) continue
			val fontImage = drawCharToImage(targetChar.toChar())
			val fontChar = CharLocation(charX, charY, fontImage.width, fontImage.height)
			
			if (fontChar.height > fontHeight)
				fontHeight = fontChar.height
			if (fontChar.height > rowHeight)
				rowHeight = fontChar.height
			
			charLocations[targetChar] = fontChar
			fontImages[targetChar] = fontImage
			
			charX += fontChar.width
			
			if (charX > 2048) {
				if (charX > textureWidth)
					textureWidth = charX
				
				charX = 0
				charY += rowHeight
				rowHeight = 0
			}
		}
		textureHeight = charY + rowHeight
		
		val bufferedImage = BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB)
		val graphics2D = bufferedImage.graphics as Graphics2D
		graphics2D.font = font
		graphics2D.color = Color(255, 255, 255, 0)
		graphics2D.fillRect(0, 0, textureWidth, textureHeight)
		graphics2D.color = Color.white
		
		for (targetChar in startChar until stopChar)
			if (fontImages[targetChar] != null && charLocations[targetChar] != null)
				graphics2D.drawImage(fontImages[targetChar], charLocations[targetChar]!!.x, charLocations[targetChar]!!.y,
					null)
		
		textureID = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), bufferedImage, true, true)
	}
	
	/**
	 * Draw a char to a buffered image
	 *
	 * @param ch char to render
	 * @return image of the char
	 */
	private fun drawCharToImage(ch: Char): BufferedImage {
		val graphics2D = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).graphics as Graphics2D
		
		graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
		graphics2D.font = font
		
		val fontMetrics = graphics2D.fontMetrics
		
		var charWidth = fontMetrics.charWidth(ch) + 8
		if (charWidth <= 0)
			charWidth = 7
		
		var charHeight = fontMetrics.height + 3
		if (charHeight <= 0)
			charHeight = font.size
		
		val fontImage = BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB)
		val graphics = fontImage.graphics as Graphics2D
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
		graphics.font = font
		graphics.color = Color.WHITE
		graphics.drawString(ch.toString(), 0, fontMetrics.ascent)
		
		return fontImage
	}
	
	fun getStringHeight(text: String): Float {
		return font.size * 0.31f
	}
	
	/**
	 * Calculate the string width of a text
	 *
	 * @param text for width calculation
	 * @return the width of the text
	 */
	fun getStringWidth(text: String): Float {
		var width = 0
		var i = 0
		var skip = false
		for (c in text.toCharArray()) {
			if(skip) {
				skip = false
				continue
			}
			if (c == '\u00a7' && i + 1 < text.length) skip = true
			i++
			val fontChar = charLocations[
			if (c.toInt() < charLocations.size)
				c.toInt()
			else
				'\u0003'.toInt()
			] ?: continue
			
			width += fontChar.width - 8
		}

		return width * 0.25f
	}
	
	/**
	 * Data class for saving char location of the font image
	 */
	private data class CharLocation(var x: Int, var y: Int, var width: Int, var height: Int)
}
