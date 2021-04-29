package com.nktfh100.KryIslands.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.nktfh100.KryIslands.info.PlayerInfo;
import com.nktfh100.KryIslands.main.KryIslands;

public class BlockPlace implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent ev) {
		Player player = ev.getPlayer();
		KryIslands plugin = KryIslands.getInstance();
		if (player.getWorld() != plugin.getIslandsManager().getIslandsWorld()) {
			return;
		}
		if (!player.hasPermission("KryIslands.admin")) {
			PlayerInfo pInfo = KryIslands.getInstance().getPlayersManager().getPlayerInfo(player);
			Location blockLoc = ev.getBlock().getLocation();
			if (pInfo.getIsland() != null && pInfo.getVisitingIsland() == null) {
				if (!pInfo.getIsland().inBounds(blockLoc)) {
					ev.setCancelled(true);
					return;
				}
			} else if (pInfo.getVisitingIsland() != null) {
				if (!pInfo.getVisitingIsland().hasPlayer(player)) {
					ev.setCancelled(true);
					return;
				} else {
					if (!pInfo.getVisitingIsland().inBounds(blockLoc)) {
						ev.setCancelled(true);
						return;
					}
				}
			}
		}
	}

}
