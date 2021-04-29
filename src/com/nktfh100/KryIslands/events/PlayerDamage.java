package com.nktfh100.KryIslands.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.nktfh100.KryIslands.info.PlayerInfo;
import com.nktfh100.KryIslands.main.KryIslands;

public class PlayerDamage implements Listener {

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent ev) {
		if (ev.getEntity() instanceof Player) {
			Player player = (Player) ev.getEntity();
			if (ev.getCause() == DamageCause.VOID && ev.getEntity().getWorld().getName().equals("KryIslands")) {
				ev.setCancelled(true);
				player.setFallDistance(0);
				PlayerInfo pInfo = KryIslands.getInstance().getPlayersManager().getPlayerInfo(player);
				if (pInfo == null) {
					return;
				}
				if (pInfo.getVisitingIsland() != null) {
					player.teleport(pInfo.getVisitingIsland().getSpawn());
				} else if (pInfo.getIsland() != null) {
					player.teleport(pInfo.getIsland().getSpawn());
				}
			}
		}
	}
}
