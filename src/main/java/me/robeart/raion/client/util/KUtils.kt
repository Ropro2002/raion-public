@file:Suppress("NOTHING_TO_INLINE")

package me.robeart.raion.client.util

import me.robeart.raion.client.imixin.IMinecraft
import me.robeart.raion.client.imixin.IMixinPlayerControllerMP
import me.robeart.raion.client.imixin.IRenderManager
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.PlayerControllerMP
import net.minecraft.entity.Entity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.TextFormatting

/**
 * @author cookiedragon234 07/May/2020
 */
fun Entity.getInterpolatedPos(): Vec3d = Vec3d(
	lastTickPosX + (posX - lastTickPosX) * (Minecraft.getMinecraft() as IMinecraft).timer.renderPartialTicks - (Minecraft.getMinecraft()
		.renderManager as IRenderManager).renderPosX,
	lastTickPosY + (posY - lastTickPosY) * (Minecraft.getMinecraft() as IMinecraft).timer.renderPartialTicks - (Minecraft.getMinecraft()
		.renderManager as IRenderManager).renderPosY,
	lastTickPosZ + (posZ - lastTickPosZ) * (Minecraft.getMinecraft() as IMinecraft).timer.renderPartialTicks - (Minecraft.getMinecraft()
		.renderManager as IRenderManager).renderPosZ
)

operator fun TextFormatting.plus(s: String): String = this.toString() + s
operator fun TextFormatting.plus(s: TextFormatting): String = this.toString() + s.toString()

/**
 * idk why this isnt already in kotlin std lol
 */
operator fun Number.plus(other: Number): Number {
	return when (this) {
		is Double -> this + other.toDouble()
		is Float  -> this + other.toFloat()
		is Int    -> this + other.toInt()
		is Long   -> this + other.toLong()
		else      -> error("Unrecognised number type ${this::class.java}")
	}
}

fun Float.isNearlyZero(): Boolean {
	return Interpolation.isNearlyZero(this)
}

fun Double.isNearlyZero(): Boolean {
	return Interpolation.isNearlyZero(this)
}

fun <T: BlockPos.MutableBlockPos> T.offsetMutable(facing: EnumFacing): T = this.apply {
	this.setPos(x + facing.xOffset, y + facing.yOffset, z + facing.zOffset)
}

fun <T: BlockPos.MutableBlockPos> T.offsetMutable(x1: Int, y1: Int, z1: Int, facing: EnumFacing): T = this.apply {
	this.setPos(x1 + facing.xOffset, y1 + facing.yOffset, z1 + facing.zOffset)
}

operator fun Vec2f.unaryMinus(): Vec2f {
	return Vec2f(-this.x, -this.y)
}

operator fun Vec2f.minus(other: Vec2f): Vec2f {
	return Vec2f(this.x - other.x, this.y - other.y)
}

inline fun <T> List<T>.forEachReversed(action: (T) -> Unit) {
	for (i in this.lastIndex downTo 0) {
		action(this[i])
	}
}

inline fun <T> mutableBlockPos(action: (mutableBlockPos: BlockPos.PooledMutableBlockPos) -> T): T {
	val pos = BlockPos.PooledMutableBlockPos.retain()
	try {
		return action(pos)
	} finally {
		pos.release()
	}
}

inline fun BlockPos.MutableBlockPos.immutable() = BlockPos(this)

inline fun PlayerControllerMP.syncCurrentPlayItem() = (this as IMixinPlayerControllerMP).invokeSyncCurrentPlayItem()

val Vec3d.isZero
	get() = this.x == 0.0 && this.y == 0.0 && this.z == 0.0
