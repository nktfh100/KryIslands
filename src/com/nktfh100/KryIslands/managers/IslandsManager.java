package com.nktfh100.KryIslands.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.nktfh100.KryIslands.info.Island;
import com.nktfh100.KryIslands.info.PlayerInfo;
import com.nktfh100.KryIslands.main.KryIslands;
import com.nktfh100.KryIslands.utils.SkyblockGenerator;
import com.nktfh100.KryIslands.utils.Utils;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;

public class IslandsManager {

	private static Integer LIMIT_XY = 29990000;

	private YamlConfiguration dataConfig;
	private KryIslands plugin;
	private World islandsWorld;

	private Location nextIsland;
	private int nextIslandKey = 0;

	private Clipboard clipboard; // The clipboard loaded with the default island schematic file

	private ArrayList<UUID> islandsRegenerating = new ArrayList<UUID>();

	public IslandsManager(KryIslands plugin) {
		this.plugin = plugin;
		try {
			File defaultSchemFile = new File(plugin.getDataFolder().getAbsolutePath() + "/default.schem");
			if (!defaultSchemFile.exists()) {
				plugin.getLogger().warning("A default schematic file couldn't be found!");
				plugin.getLogger().warning("Please place 'default.schem' in the KryIslands plugin folder");
				plugin.getLogger().warning("Disabeling...");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
				return;
			}
			ClipboardFormat format = ClipboardFormats.findByFile(defaultSchemFile);
			ClipboardReader reader;
			reader = format.getReader(new FileInputStream(defaultSchemFile));

			this.clipboard = reader.read();
		} catch (IOException e) {
			plugin.getLogger().warning("Couldn't load the default schematic file!");
			plugin.getLogger().warning("Disabeling...");
			e.printStackTrace();
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}

	public void loadData() {
		File dataConfigFIle = new File(this.plugin.getDataFolder(), "data.yml");
		if (!dataConfigFIle.exists()) {
			try {
				this.plugin.saveResource("data.yml", false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.dataConfig = YamlConfiguration.loadConfiguration(dataConfigFIle);

		if (this.dataConfig.isSet("nextIsland")) {
			nextIsland = new Location(this.islandsWorld, this.dataConfig.getInt("nextIsland.x"), plugin.getConfigManager().getIslandHeight(), this.dataConfig.getInt("nextIsland.z"));
		} else {
			nextIsland = new Location(this.islandsWorld, -LIMIT_XY, plugin.getConfigManager().getIslandHeight(), -LIMIT_XY);
		}
		if (this.dataConfig.isSet("nextIsland.key")) {
			nextIslandKey = this.dataConfig.getInt("nextIsland.key", 0);
		}
	}

	public void saveData() {
		if (this.dataConfig == null) {
			return;
		}
		this.dataConfig.set("nextIsland.x", nextIsland.getBlockX());
		this.dataConfig.set("nextIsland.y", nextIsland.getBlockY());
		this.dataConfig.set("nextIsland.z", nextIsland.getBlockZ());
		this.dataConfig.set("nextIsland.key", nextIslandKey);
		try {
			this.dataConfig.save(plugin.getDataFolder() + File.separator + "data.yml");
		} catch (IOException e) {
			plugin.getLogger().warning("Could not save data.yml");
		}
	}

	// Only for online players
	public void saveAllIslandsData() {
		for (PlayerInfo pInfo : plugin.getPlayersManager().getPlayers()) {
			pInfo.getIsland().saveIsland();
		}
	}

	public void loadWorld() {
		World world = Bukkit.getWorld("KryIslands");
		if (world == null) {
			world = new WorldCreator("KryIslands").generator(plugin.getDefaultWorldGenerator("KryIslands", null)).environment(Environment.NORMAL).createWorld();
		} else {
			if (!(world.getGenerator() instanceof SkyblockGenerator)) {
				plugin.getLogger().warning("Warning! You are not using KryIslands's world generator!");
				plugin.getLogger().warning("Add the following to your 'bukkit.yml' file:");
				plugin.getLogger().warning("worlds:");
				plugin.getLogger().warning("  KryIslands: ");
				plugin.getLogger().warning("    generator: KryIslands");
				plugin.getLogger().warning("Or if you are using multiverse-core:");
				plugin.getLogger().warning("Set KryIslands's generator to 'KryIslands'");
			}
		}

		this.islandsWorld = world;
	}

	@SuppressWarnings("deprecation")
	public void pasteSchem(Location loc) {
		// We need to adapt our world into a format that worldedit accepts. This looks
		// like this:
		// Ensure it is using com.sk89q... otherwise we'll just be adapting a world into
		// the same world.
		com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(this.islandsWorld);

		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, -1);

		// Saves our operation and builds the paste - ready to be completed.
		Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getZ())).ignoreAirBlocks(true).build();

		try { // This simply completes our paste and then cleans up.
			Operations.complete(operation);
			editSession.flushSession();
		} catch (WorldEditException e) {
			e.printStackTrace();
		}
	}

	public Island generateIsland(Player player) {
		Location loc = this.nextIsland;
		loc.setWorld(this.islandsWorld);
		this.pasteSchem(loc);
		PlayerInfo pInfo = plugin.getPlayersManager().getPlayerInfo(player);
		player.setFallDistance(0);

		int islandWidth = plugin.getConfigManager().getIslandWidth();
		int islandHeight = plugin.getConfigManager().getIslandHeight();

		Location np1 = new Location(this.islandsWorld, loc.getX() - (islandWidth / 2), 0, loc.getZ() - (islandWidth / 2));
		Location np2 = new Location(this.islandsWorld, loc.getX() + (islandWidth / 2), this.islandsWorld.getMaxHeight(), loc.getZ() + (islandWidth / 2));

		Island newIs = new Island(np1, np2, loc, nextIslandKey, player, this.islandsWorld, "default.schem", this.dataConfig, plugin);
		nextIslandKey++;

		pInfo.setIsland(newIs);
		pInfo.setVisitingIsland(null);

		// Set the next island location
		if (loc.getX() < LIMIT_XY) {
			nextIsland = new Location(this.islandsWorld, loc.getX() + islandWidth, islandHeight, loc.getZ());
		} else {
			nextIsland = new Location(this.islandsWorld, -LIMIT_XY, islandHeight, loc.getZ() + islandWidth);
		}
		this.saveData();
		newIs.saveIsland();
		pInfo.saveData();
		new BukkitRunnable() {
			@Override
			public void run() {
				player.teleport(loc);
			}
		}.runTask(plugin);
		return newIs;
	}

	public void regenerateIsland(Player player) {
		PlayerInfo pInfo = plugin.getPlayersManager().getPlayerInfo(player);
		islandsRegenerating.add(player.getUniqueId());
		Island island = pInfo.getIsland();
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
		if (island.inBounds(player.getLocation())) {
			player.teleport(plugin.getConfigManager().getLobby());
		}
		for (UUID uuid : island.getMembers()) {
			if (uuid == null) {
				continue;
			}
			Player player_ = Bukkit.getServer().getPlayer(uuid);
			if (player_ != null && player_.isOnline()) {
				PlayerInfo pInfo_ = plugin.getPlayersManager().getPlayerByUUID(uuid.toString());
				if (pInfo_.getVisitingIsland() != null && pInfo_.getVisitingIsland().getKey() == pInfo.getIsland().getKey()) {
					player_.teleport(pInfo_.getIsland().getSpawn());
				}
			}
		}

		UUID uuid = player.getUniqueId();
		ArrayList<Chunk> chunks_ = Utils.getIslandChunks(island);
		int chunkCleanRate = plugin.getConfigManager().getChunkCleanRate();
		plugin.getMessagesManager().sendMessage(player, "regenerating", Math.round((float) chunks_.size() / chunkCleanRate) + "");
		plugin.getLogger().info("Resetting " + player.getName() + "'s island (" + chunks_.size() + " chunks)");
		plugin.getLogger().info("Clean rate is " + chunkCleanRate + " chunks per second. Should take ~" + Math.round((float) chunks_.size() / chunkCleanRate) + "s");
		new BukkitRunnable() {
			private Boolean canContinue = true;
			private ArrayList<Chunk> chunks = chunks_;

			@Override
			public void run() {
				if (chunks.isEmpty()) {
					plugin.getIslandsManager().pasteSchem(island.getSpawn());
					islandsRegenerating.remove(uuid);
					if (player.isOnline()) {
						plugin.getMessagesManager().sendMessage(player, "regenerate-success");
					}
					this.cancel();
					return;
				} else if (canContinue) {
					canContinue = false;
					Iterator<Chunk> iterator = chunks.iterator();
					int i = 0;
					while (iterator.hasNext()) {
						Chunk chunk = iterator.next();
						if (i >= chunkCleanRate) {
							canContinue = true;
							return;
						}
						Utils.regenerateChunk(chunk);
						iterator.remove();
						i++;
					}
				}
			}
		}.runTaskTimer(plugin, 1L, 20L);
	}

	public void playerJoined(PlayerInfo pInfo) {
		String playerIslandkey_ = "players." + pInfo.getPlayer().getUniqueId().toString() + ".island";
		if (this.dataConfig.isSet(playerIslandkey_)) {
			int islandKey = this.dataConfig.getInt(playerIslandkey_);
			Island island = new Island(islandKey, this.islandsWorld, this.dataConfig, plugin);
			pInfo.setIsland(island);
			if (this.islandsRegenerating.contains(pInfo.getPlayer().getUniqueId())) {
				pInfo.getPlayer().teleport(plugin.getConfigManager().getLobby());
			} else {
				pInfo.getPlayer().teleport(island.getSpawn());
			}
		} else {
			// Create a new island for this player
			new BukkitRunnable() {
				@Override
				public void run() {
					plugin.getLogger().info("Creating a new island for " + pInfo.getPlayer().getName());
					generateIsland(pInfo.getPlayer());
				}
			}.runTaskLaterAsynchronously(plugin, 2L);
		}
	}

	public Boolean islandExists(UUID uuid) {
		int key = dataConfig.getInt("players." + uuid.toString() + ".island", -1);
		if (key == -1) {
			return false;
		}
		return true;
	}

	public int getIslandKey(UUID uuid) {
		return dataConfig.getInt("players." + uuid.toString() + ".island", -1);
	}

	public Boolean isIslandRegenerating(UUID targetUUID) {
		String targetUUIDStr = targetUUID.toString();
		for (UUID uuid : this.islandsRegenerating) {
			if (uuid.toString().equals(targetUUIDStr)) {
				return true;
			}
		}
		return false;
	}

	public World getIslandsWorld() {
		return islandsWorld;
	}

	public YamlConfiguration getDataConfig() {
		return this.dataConfig;
	}

	public ArrayList<UUID> getIslandsRegenerating() {
		return islandsRegenerating;
	}

}
