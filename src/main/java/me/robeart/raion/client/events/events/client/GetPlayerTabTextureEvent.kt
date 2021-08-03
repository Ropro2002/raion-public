package me.robeart.raion.client.events.events.client

import net.minecraft.client.network.NetworkPlayerInfo

/**
 * @author cookiedragon234 03/Mar/2020
 */
data class GetPlayerTabTextureEvent(val networkPlayerInfo: NetworkPlayerInfo, var shouldLoad: Boolean)
