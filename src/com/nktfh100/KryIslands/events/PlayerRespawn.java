package com.nktfh100.KryIslands.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.nktfh100.KryIslands.info.PlayerInfo;
import com.nktfh100.KryIslands.main.KryIslands;

public class PlayerRespawn implements Listener {
	@EventHandler
	public void onRespawn(PlayerRespawnEvent ev) {
		Player player = ev.getPlayer();
		KryIslands plugin = KryIslands.getInstance();
		if (player.getWorld() != plugin.getIslandsManager().getIslandsWorld()) {
			return;
		}
		PlayerInfo pInfo = KryIslands.getInstance().getPlayersManager().getPlayerInfo(player);
		if (pInfo.getIsland() != null && pInfo.getVisitingIsland() == null) {
			ev.setRespawnLocation(pInfo.getIsland().getSpawn());
		} else if (pInfo.getVisitingIsland() != null) {
			ev.setRespawnLocation(pInfo.getVisitingIsland().getSpawn());
		}
	}
}
