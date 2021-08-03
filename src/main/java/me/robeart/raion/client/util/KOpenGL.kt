package me.robeart.raion.client.util

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import java.awt.Color

/**
 * @author cookiedragon234 19/Jun/2020
 */
private val mc = Minecraft.getMinecraft()

inline fun <T> draw(translate: Boolean = false, glMode: Int? = null, format: VertexFormat? = null, action: BufferBuilder.() -> T): T {
	val ts = Tessellator.getInstance()
	val bb = ts.buffer
	try {
		if (glMode != null && format != null) bb.begin(glMode, format)
		if (translate) bb.translateToRender()
		return action(bb)
	} finally {
		bb.setTranslation(0.0, 0.0, 0.0)
		ts.draw()
	}
}

inline fun <T: BufferBuilder> T.vertex(action: BufferBuilder.() -> Unit): T = this.apply {
	try {
		action(this)
	} finally {
		endVertex()
	}
}

fun <T: BufferBuilder> T.translateToRender(): T = this.apply {
	this.setTranslation(
		-mc.renderManager.viewerPosX,
		-mc.renderManager.viewerPosY,
		-mc.renderManager.viewerPosZ
	)
}

fun <T: BufferBuilder> T.pos(pos: Vec3d, xOffset: Double = 0.0, yOffset: Double = xOffset, zOffset: Double = xOffset): T = this.apply {
	this.pos(pos.x + xOffset, pos.y + yOffset, pos.z + zOffset)
}

fun <T: BufferBuilder> T.pos(pos: Vec3i, xOffset: Double = 0.0, yOffset: Double = xOffset, zOffset: Double = xOffset): T = this.apply {
	this.pos(pos.x.toDouble() + xOffset, pos.y.toDouble() + yOffset, pos.z.toDouble() + zOffset)
}

fun <T: BufferBuilder> T.color(color: Color): T = this.apply {
	this.color(color.red, color.green, color.blue, color.alpha)
}

fun <T: BufferBuilder> T.color(color: Int): T = this.apply {
	val alpha = (color shr 24 and 255)
	val red = (color shr 16 and 255)
	val green = (color shr 8 and 255)
	val blue = (color and 255)
	color(alpha, red, green, blue)
}

fun <T: BufferBuilder> T.bb(bb: AxisAlignedBB, red: Float? = null, green: Float? = null, blue: Float? = null, alpha: Float? = null): T = this.apply {
	vertex {
		pos(bb.minX, bb.minY, bb.minZ)
		if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
	}
	vertex {
		pos(bb.minX, bb.maxY, bb.minZ)
		if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
	}
	vertex {
		pos(bb.maxX, bb.minY, bb.minZ)
		if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
	}
	vertex {
		pos(bb.maxX, bb.maxY, bb.minZ)
		if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
	}
	vertex {
		pos(bb.maxX, bb.minY, bb.maxZ)
		if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
	}
	vertex {
		pos(bb.maxX, bb.maxY, bb.maxZ)
		if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
	}
	vertex {
		pos(bb.minX, bb.minY, bb.maxZ)
		if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
	}
	vertex {
		pos(bb.minX, bb.maxY, bb.maxZ)
		if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
	}
}

fun <T: BufferBuilder> T.bb(bb: AxisAlignedBB, red: Int? = null, green: Int? = null, blue: Int? = null, alpha: Int? = null): T = this.apply {
	vertex {
		pos(bb.minX, bb.minY, bb.minZ)
		if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
	}
	vertex {
		pos(bb.minX, bb.maxY, bb.minZ)
		if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
	}
	vertex {
		pos(bb.maxX, bb.minY, bb.minZ)
		if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
	}
	vertex {
		pos(bb.maxX, bb.maxY, bb.minZ)
		if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
	}
	vertex {
		pos(bb.maxX, bb.minY, bb.maxZ)
		if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
	}
	vertex {
		pos(bb.maxX, bb.maxY, bb.maxZ)
		if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
	}
	vertex {
		pos(bb.minX, bb.minY, bb.maxZ)
		if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
	}
	vertex {
		pos(bb.minX, bb.maxY, bb.maxZ)
		if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
	}
}
