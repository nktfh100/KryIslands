package com.nktfh100.KryIslands.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;

import com.nktfh100.KryIslands.main.KryIslands;

public class PlayerEnterPortal implements Listener {
	@EventHandler
	public void enterPortal(EntityPortalEnterEvent ev) {
		if (ev.getEntity() instanceof Player) {
			KryIslands plugin = KryIslands.getInstance();
			if (ev.getEntity().getWorld() == plugin.getIslandsManager().getIslandsWorld()) {
				Player player = (Player) ev.getEntity();
				player.teleport(plugin.getConfigManager().getLobby());
				return;
			}
		}
	}
}
