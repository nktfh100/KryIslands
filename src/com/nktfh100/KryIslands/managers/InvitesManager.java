package com.nktfh100.KryIslands.managers;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.nktfh100.KryIslands.info.IslandInvite;
import com.nktfh100.KryIslands.main.KryIslands;

public class InvitesManager {

	KryIslands plugin;

	private ArrayList<IslandInvite> invites = new ArrayList<IslandInvite>();

	public InvitesManager(KryIslands plugin) {
		this.plugin = plugin;
		new BukkitRunnable() {
			@Override
			public void run() {
				Iterator<IslandInvite> iterator = invites.iterator();
				while (iterator.hasNext()) {
					IslandInvite invite = iterator.next();
					if (invite.isExpired()) {
						invite.expired();
						iterator.remove();
					}
				}
			}
		}.runTaskTimerAsynchronously(plugin, 60L, 20L);
	}

	public void addInvite(IslandInvite invite) {
		this.invites.add(invite);
	}

	public void removeInvite(IslandInvite invite) {
		this.invites.remove(invite);
	}

	public IslandInvite getInvite(Player player1, Player player2) {
		for (IslandInvite invite : invites) {
			if (invite.getPlayer1().getName().equals(player1.getName()) && invite.getPlayer2().getName().equals(player2.getName())) {
				return invite;
			}
		}
		return null;
	}

	public ArrayList<IslandInvite> getInvites() {
		return invites;
	}

}
