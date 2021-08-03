package me.robeart.raion.client.module.combat

import me.robeart.raion.client.events.events.player.OnUpdateEvent
import me.robeart.raion.client.module.Module
import me.robeart.raion.client.util.minecraft.MinecraftUtils
import me.robeart.raion.client.value.BooleanValue
import me.robeart.raion.client.value.IntValue
import me.robeart.raion.client.value.kotlin.ValueDelegate
import net.minecraft.init.Items
import net.minecraft.inventory.ClickType
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener

object AutoTotemModule : Module("AutoTotem", "Automatically equips a Totem of Undying in your offhand", Category.COMBAT) {

    //val conditionsS = BooleanValue("Conditions", "Conditions to activating auto totem", true)
    //val conditions by ValueDelegate(conditionsS)
    val caCheck by ValueDelegate(BooleanValue("CA Offhand", "Put crystal in your offhand if above hp check", false))
    //val caTargetCheck by ValueDelegate(BooleanValue("CA Target Check", "Enable if no CA targets in range", false, conditionsS))
    val health by ValueDelegate(IntValue("Health", 20, 0, 36, 1))

    //private var noTargetFor: Long = -1

    /*fun areConditionsMet(): Boolean {
        if (conditions) {
            if (caTargetCheck) {
                val target = CrystalAuraModule.INSTANCE.target
                if (target == null) {
                    if (noTargetFor == -1L) {
                        noTargetFor = System.currentTimeMillis()
                    } else if (System.currentTimeMillis() - noTargetFor >= 1000) {
                        return true
                    }
                } else {
                    noTargetFor = -1
                }
            }
            if (mc.player.health + mc.player.absorptionAmount <= health) {
                return true
            }
        }
        return true
    }*/

    @Listener
    fun onUpdate(event: OnUpdateEvent?) {
        val offhand = mc.player.heldItemOffhand.item
        val slot = if (mc.player.health + mc.player.absorptionAmount > health && (caCheck && (CrystalAura2.state || CrystalAuraModule.INSTANCE.state))) {
            if(offhand != Items.END_CRYSTAL) MinecraftUtils.getSlotOfItem(Items.END_CRYSTAL) else return
        } else if(offhand != Items.TOTEM_OF_UNDYING) MinecraftUtils.getSlotOfItem(Items.TOTEM_OF_UNDYING) else return
        if (slot != -1) {
            if (mc.currentScreen != null) return
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player)
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, mc.player)
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player)
            mc.playerController.updateController()
        }
    }

    override fun getHudInfo(): String {
        return MinecraftUtils.getItemCount(Items.TOTEM_OF_UNDYING).toString()
    }
}