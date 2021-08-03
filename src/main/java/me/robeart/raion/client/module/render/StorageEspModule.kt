package me.robeart.raion.client.module.render

import me.robeart.raion.client.events.events.render.Render3DEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.minecraft.RenderUtils
import net.minecraft.tileentity.*
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener

/**
 * @author cookiedragon234 07/May/2020
 */
object StorageEspModule: Module("StorageESP", "Highlights blocks that store items", Category.RENDER) {
	
	@Listener
	fun onRender(event: Render3DEvent) {
		mc.world.loadedTileEntityList.forEach { tileEntity ->
			getColour(tileEntity)?.let { colour ->
				RenderUtils.blockEsp(tileEntity.pos, colour, 1.0, 1.0)
			}
		}
	}
	
	private fun getColour(tileEntity: TileEntity): Int? = when (tileEntity) {
		is TileEntityChest      -> 0x45fcc203
		is TileEntityEnderChest -> 0x4503fcb1
		is TileEntityShulkerBox -> 0x459d03fc
		is TileEntityHopper     -> 0x45595963
		else                    -> null
	}
}
