package com.nktfh100.KryIslands.utils;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.World;

import com.nktfh100.KryIslands.info.Island;
import com.nktfh100.KryIslands.main.KryIslands;

public class Utils {
	public static ArrayList<Chunk> getIslandChunks(Island island) {
		ArrayList<Chunk> chunks = new ArrayList<>();
		World world = KryIslands.getInstance().getIslandsManager().getIslandsWorld();
		for (int x = island.getMinX() >> 4; x <= island.getMaxX() >> 4; x++) {
			for (int z = island.getMinZ() >> 4; z <= island.getMaxZ() >> 4; z++) {
				Chunk chunk = world.getChunkAt(x, z);
				if (!chunks.contains(chunk)) {
					chunks.add(chunk);
				}
			}
		}
		return chunks;
	}

	@SuppressWarnings("deprecation")
	public static void regenerateChunk(Chunk chunk) {
		int bx = chunk.getX() << 4;
		int bz = chunk.getZ() << 4;
		try {
			com.sk89q.worldedit.math.BlockVector3 pt1 = com.sk89q.worldedit.math.BlockVector3.at(bx, 0, bz);
			com.sk89q.worldedit.math.BlockVector3 pt2 = com.sk89q.worldedit.math.BlockVector3.at(bx + 15, 256, bz + 15);
			com.sk89q.worldedit.bukkit.BukkitWorld world = new com.sk89q.worldedit.bukkit.BukkitWorld(chunk.getWorld());
			com.sk89q.worldedit.regions.CuboidRegion region = new com.sk89q.worldedit.regions.CuboidRegion(world, pt1, pt2);
			com.sk89q.worldedit.EditSession session = com.sk89q.worldedit.WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, 65536);
			world.regenerate(region, session);
			session.flushSession();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
