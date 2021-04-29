package com.nktfh100.KryIslands.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.nktfh100.KryIslands.main.KryIslands;

import net.md_5.bungee.api.ChatColor;

public class ConfigManager {

	private KryIslands plugin;

	private String prefix;
	private Integer islandHeight;
	private Integer islandWidth;
	private Location lobby;
	private Integer chunkCleanRate;
	private Integer inviteTimeout;

	public ConfigManager(KryIslands plugin) {
		this.plugin = plugin;
		plugin.saveDefaultConfig();
	}

	public void loadConfig() {
		this.loadConfigVars();
	}

	public void loadConfigVars() {

		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();

		this.prefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefix", ""));
		this.islandHeight = config.getInt("islandHeight", 70);
		this.islandWidth = config.getInt("islandWidth", 1000);
		this.chunkCleanRate = config.getInt("chunkCleanRate", 1);
		this.inviteTimeout = config.getInt("inviteTimeout", 60);

		World world = Bukkit.getWorld(config.getString("lobby.world", "world"));
		if (world == null) {
			world = Bukkit.getWorlds().get(0);
		}
		this.lobby = new Location(world, config.getDouble("lobby.x", 0), config.getDouble("lobby.y", 0), config.getDouble("lobby.z", 0), (float) config.getDouble("lobby.yaw", 0),
				(float) config.getDouble("lobby.pitch", 0));

		if (this.lobby.getBlockX() == 0 && this.lobby.getBlockY() == 0 && this.lobby.getBlockZ() == 0) {
			this.lobby = this.lobby.getWorld().getSpawnLocation();
		}
	}

	public String getPrefix() {
		return this.prefix;
	}

	public Integer getIslandHeight() {
		return islandHeight;
	}

	public Integer getIslandWidth() {
		return islandWidth;
	}

	public Location getLobby() {
		return lobby;
	}

	public void setLobby(Location to) {
		this.lobby = to;
	}

	public Integer getChunkCleanRate() {
		return chunkCleanRate;
	}

	public Integer getInviteTimeout() {
		return inviteTimeout;
	}

}
