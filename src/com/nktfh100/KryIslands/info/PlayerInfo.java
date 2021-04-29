package com.nktfh100.KryIslands.info;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.nktfh100.KryIslands.main.KryIslands;

public class PlayerInfo {

	private Player player;

	private Island visitingIsland = null;
	private Island island = null;

	public PlayerInfo(Player player) {
		this.player = player;
	}

	public void saveData() {
		YamlConfiguration dataConfig = KryIslands.getInstance().getIslandsManager().getDataConfig();

		dataConfig.set("players." + this.player.getUniqueId().toString() + ".island", this.island.getKey());
		try {
			dataConfig.save(KryIslands.getInstance().getDataFolder() + File.separator + "data.yml");
		} catch (IOException e) {
			KryIslands.getInstance().getLogger().warning("Could not save data.yml");
		}
	}

	public Player getPlayer() {
		return this.player;
	}

	public Island getVisitingIsland() {
		return visitingIsland;
	}

	public void setVisitingIsland(Island visitingIsland) {
		this.visitingIsland = visitingIsland;
	}

	public Island getIsland() {
		return this.island;
	}

	public void setIsland(Island island) {
		this.island = island;
	}
}
