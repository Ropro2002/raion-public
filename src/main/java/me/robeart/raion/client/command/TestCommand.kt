package me.robeart.raion.client.command

import me.robeart.raion.client.util.ChatUtils
import me.robeart.raion.client.util.minecraft.MinecraftUtils
import net.minecraft.entity.Entity

/**
 * @author Robeart 25/07/2020
 */
object TestCommand: Command(
        "test",
        "test",
        "test"
) {
    override fun call(args: Array<String>) {
        if(mc.objectMouseOver.entityHit != null) {
            lookAtPacket(mc.objectMouseOver.entityHit)
        }
    }

    private fun lookAtPacket(entity: Entity) {
        var dirx: Double = mc.player.posX - entity.posX
        var diry: Double = mc.player.posY - entity.posY + entity.eyeHeight
        var dirz: Double = mc.player.posZ - entity.posZ

        val len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz)

        dirx /= len
        diry /= len
        dirz /= len

        var pitch = Math.asin(diry)
        var yaw = Math.atan2(dirz, dirx)

        //to degree

        //to degree
        pitch = pitch * 180.0 / Math.PI
        yaw = yaw * 180.0 / Math.PI

        yaw += 90.0

        ChatUtils.message("$yaw $pitch")


        /*val x = entity.posX - mc.player.posX
        val y = entity.posY + entity.eyeHeight - (mc.player.posY + mc.player.eyeHeight) - 0.85f
        val z = entity.posZ - mc.player.posZ
        val double = Math.sqrt((x * x) + (z * z))
        var yaw = Math.atan2(z, x) * 180.0 / Math.PI
        val pitch = -Math.atan2(y, double) * 180.0 / Math.PI
        yaw += 90.0f
        yaw -= mc.player.rotationYaw
        if(yaw < -180f || yaw > 180f) {
            val n11 = Math.round(Math.abs(yaw / 360f))
            if(yaw < 0.0f) yaw += 360.0f * n11
            else yaw -= 360.0f * n11
        }
        ChatUtils.message("$yaw $pitch")*/
    }
}