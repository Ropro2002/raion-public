package me.robeart.raion.client.command

import com.mojang.authlib.GameProfile
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import me.robeart.raion.client.util.ChatUtils
import me.robeart.raion.client.util.SkinManager
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagList
import net.minecraft.server.MinecraftServer
import net.minecraft.server.management.PlayerProfileCache
import net.minecraft.util.EnumHand
import java.io.File
import java.util.UUID
import kotlin.concurrent.thread

/**
 * @author cookiedragon234 08/Jun/2020
 */
@Suppress("SpellCheckingInspection")
object FakePlayerCommand: Command("fakeplayer", "Spawn a fake player", "fakeplayer {add/remove}? {name}?") {
	private val yggdrasilauthenticationservice by lazy {
		YggdrasilAuthenticationService(
			mc.proxy,
			UUID.randomUUID().toString()
		)
	}
	private val minecraftsessionservice by lazy { yggdrasilauthenticationservice.createMinecraftSessionService() }
	private val gameprofilerepository by lazy { yggdrasilauthenticationservice.createProfileRepository() }
	private val playerprofilecache by lazy {
		PlayerProfileCache(
			gameprofilerepository,
			File(mc.gameDir, MinecraftServer.USER_CACHE_FILE.name)
		)
	}
	
	private val playerInfoMapF by lazy {
		NetHandlerPlayClient::class.java.declaredFields.firstOrNull { it.type == Map::class.java }?.also {
			it.isAccessible = true
		}
	}
	private val gameProfileF by lazy {
		EntityPlayer::class.java.declaredFields.firstOrNull { it.type == GameProfile::class.java }?.also {
			it.isAccessible = true
		}
	}
	
	override fun call(args: Array<out String>) {
		if (mc.world == null) return
		
		val control = if (args.size < 2) "add" else args[0]
		val name = if (args.size == 1) args[0] else if (args.size > 1) args[1] else "Player"
		
		if (control == "add") {
			val health = mc.player.health
			val posX = mc.player.posX
			val posY = mc.player.posY
			val posZ = mc.player.posZ
			val prevPosX = mc.player.prevPosX
			val prevPosY = mc.player.prevPosY
			val prevPosZ = mc.player.prevPosZ
			val lastTickPosX = mc.player.lastTickPosX
			val lastTickPosY = mc.player.lastTickPosY
			val lastTickPosZ = mc.player.lastTickPosZ
			val rotationYaw = mc.player.rotationYaw
			val rotationYawHead = mc.player.rotationYawHead
			val rotationPitch = mc.player.rotationPitch
			val prevRotationYaw = mc.player.prevRotationYaw
			val prevRotationYawHead = mc.player.prevRotationYawHead
			val prevRotationPitch = mc.player.prevRotationPitch
			
			thread(start = true, isDaemon = true) {
				val profile = playerprofilecache.getGameProfileForUsername(name)
					?: GameProfile(EntityPlayer.getOfflineUUID(name), name)
				println("$gameProfileF")
				SkinManager.updateSkin(profile, profile.id.toString().replace("-", ""))
				//println("ID: " + profile.id.toString().replace("-",""))
				
				if (mc.world == null || mc.player == null) return@thread
				
				val connection = mc.player.connection
				if (connection.getPlayerInfo(profile.id) == null && playerInfoMapF != null) {
					val map = playerInfoMapF!!.get(connection) as MutableMap<UUID, NetworkPlayerInfo>
					map[profile.id] = NetworkPlayerInfo(profile)
					println("Added to map")
				}
				
				val player = EntityOtherPlayerMP(mc.world, profile)
				
				player.health = health
				player.posX = posX
				player.posY = posY
				player.posZ = posZ
				player.prevPosX = prevPosX
				player.prevPosY = prevPosY
				player.prevPosZ = prevPosZ
				player.lastTickPosX = lastTickPosX
				player.lastTickPosY = lastTickPosY
				player.lastTickPosZ = lastTickPosZ
				player.rotationYaw = rotationYaw
				player.rotationYawHead = rotationYawHead
				player.rotationPitch = rotationPitch
				player.prevRotationYaw = prevRotationYaw
				player.prevRotationYawHead = prevRotationYawHead
				player.prevRotationPitch = prevRotationPitch
				player.inventory.readFromNBT(NBTTagList().also { mc.player.inventory.writeToNBT(it) })
				player.setHeldItem(EnumHand.MAIN_HAND, mc.player.heldItemMainhand)
				player.setHeldItem(EnumHand.OFF_HAND, mc.player.heldItemOffhand)
				
				mc.addScheduledTask {
					for (i in -100 downTo -500) {
						if (mc.world.getEntityByID(i) == null) {
							mc.world.addEntityToWorld(i, player)
							ChatUtils.message("""Player [${profile.name}] was added to the world""")
							player.isInvisible = false
							player.isDead = false
							return@addScheduledTask
						}
					}
					ChatUtils.error("No available slots")
				}
			}
		} else if (control == "remove" || control == "delete") {
			for (entity in mc.world.loadedEntityList) {
				if (entity is EntityOtherPlayerMP) {
					if (entity.name == name) {
						mc.world.removeEntity(entity)
						ChatUtils.message("""Removed player [${entity.name}] from the world""")
						return
					}
				}
			}
			ChatUtils.error("Couldnt find player [$name]")
		} else {
			ChatUtils.error(this.usage)
			return
		}
	}
}
