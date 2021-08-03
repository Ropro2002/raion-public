package me.robeart.raion.client.util;

import java.util.Random;

/**
 * @author cookiedragon234 09/Jun/2020
 */
public class WorldGenerationUtils {
	public static boolean isSlimeChunk(long seed, int chunkX, int chunkZ) {
		long lchunkX = chunkX;
		long lchunkZ = chunkZ;
		Random rnd = new Random(
			seed +
				(int) (lchunkX * lchunkX * 0x4c1906) +
				(int) (lchunkX * 0x5ac0db) +
				(int) (lchunkZ * lchunkZ) * 0x4307a7L +
				(int) (lchunkZ * 0x5f24f) ^ 0x3ad8025f
		);
		
		return rnd.nextInt(10) == 0;
	}
}
