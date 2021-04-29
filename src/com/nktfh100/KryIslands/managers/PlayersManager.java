package com.nktfh100.KryIslands.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.nktfh100.KryIslands.info.PlayerInfo;
import com.nktfh100.KryIslands.main.KryIslands;

public class PlayersManager implements Listener {

	private KryIslands plugin;
	private HashMap<String, PlayerInfo> players = new HashMap<>();

	public PlayersManager(KryIslands plugin) {
		this.plugin = plugin;
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			players.put(player.getUniqueId().toString(), new PlayerInfo(player));
		}
		for (PlayerInfo pInfo : players.values()) {
			plugin.getIslandsManager().playerJoined(pInfo);
		}
	}

	public PlayerInfo getPlayerInfo(Player player) {
		return players.get(player.getUniqueId().toString());
	}

	public PlayerInfo getPlayerByUUID(String uuid) {
		return this.players.get(uuid);
	}

	public List<PlayerInfo> getPlayers() {
		List<PlayerInfo> players_ = new ArrayList<PlayerInfo>(this.players.values());
		return players_;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent ev) {
		players.put(ev.getPlayer().getUniqueId().toString(), new PlayerInfo(ev.getPlayer()));
		plugin.getIslandsManager().playerJoined(this.getPlayerInfo(ev.getPlayer()));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerInfo pInfo = this.players.get(player.getUniqueId().toString());
		if (pInfo == null) {
			return;
		}
//		if (pInfo.getIsland() != null) {
//			pInfo.getIsland().saveIsland();
//			pInfo.saveData();
//		}
		players.remove(player.getUniqueId().toString());
	}

	public void savePlayersData() {
		for (PlayerInfo pInfo : this.players.values()) {
			pInfo.saveData();
		}
	}
}
