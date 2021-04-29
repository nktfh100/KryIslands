package com.nktfh100.KryIslands.utils;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SkyblockGenerator extends ChunkGenerator {

	// For void worlds

	public byte[][] blockSections;

	@SuppressWarnings("deprecation")
	@Override
	public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid) {
		ChunkData chunk = createChunkData(world);

		Biome biome = Biome.PLAINS;
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				if (world.getEnvironment() != Environment.NETHER) {
					biomeGrid.setBiome(x, z, biome);
				}
			}
		}

		return chunk;
	}

	public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomeGrid) {
		if (blockSections == null) {
			blockSections = new byte[world.getMaxHeight() / 16][];
		}
		return blockSections;
	}

	@Override
	public boolean canSpawn(World world, int x, int z) {
		return true;
	}

	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		return Collections.emptyList();
	}

}