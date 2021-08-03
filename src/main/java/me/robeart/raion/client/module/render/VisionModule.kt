package me.robeart.raion.client.module.render

import me.robeart.raion.client.events.events.render.BlockStateAtEntityViewpointEvent
import me.robeart.raion.client.events.events.render.SetupFogEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener


/**
 * @author cookiedragon234 27/Mar/2020
 */
object VisionModule: Module("Vision", "Makes things easy to see", Category.RENDER) {
	val brightness by ValueDelegate(BooleanValue("Brightness", true))
	val barriers by ValueDelegate(BooleanValue("Barriers", true))
	val noFog by ValueDelegate(BooleanValue("No Fog", true))

	val barrierModel: IBakedModel by lazy {
		mc.renderItem.itemModelMesher.getItemModel(ItemStack(Blocks.BARRIER))
	}

	override fun onEnable() {
		mc.renderGlobal?.loadRenderers()
	}

	override fun onDisable() {
		mc.renderGlobal?.loadRenderers()
	}


	@Listener
	private fun onSetupFog(event: SetupFogEvent) {
		if (noFog) {
			event.isCanceled = true
		}
	}

	@Listener
	private fun onBlockStateAtEntityViewpoint(event: BlockStateAtEntityViewpointEvent) {
		if (noFog) {
			event.setiBlockState(Blocks.AIR as IBlockState)
		}
	}

}
