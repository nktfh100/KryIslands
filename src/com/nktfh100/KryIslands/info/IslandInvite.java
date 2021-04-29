package com.nktfh100.KryIslands.info;

import org.bukkit.entity.Player;

import com.nktfh100.KryIslands.main.KryIslands;

public class IslandInvite {

	private KryIslands plugin;
	private long sentTime = System.currentTimeMillis();
	private Island island;
	private PlayerInfo pInfo1; // Sender
	private PlayerInfo pInfo2; // Received the invite

	private Player player1;
	private Player player2;

	public IslandInvite(Player player1, Player player2) {
		this.plugin = KryIslands.getInstance();
		this.pInfo1 = plugin.getPlayersManager().getPlayerInfo(player1);
		this.pInfo2 = plugin.getPlayersManager().getPlayerInfo(player2);
		this.island = this.pInfo1.getIsland();

		this.player1 = player1;
		this.player2 = player2;
	}

	public long getSentTime() {
		return sentTime;
	}

	public void acceptInvite() {
		plugin.getMessagesManager().sendMessage(this.player1, "island-invite-accepted-sender", this.player2.getName());
		plugin.getMessagesManager().sendMessage(this.player2, "island-invite-accepted", this.player1.getName());
		this.island.addMember(player2);
		this.island.saveIsland();
	}

	public void declineInvite() {
		plugin.getMessagesManager().sendMessage(this.player1, "island-invite-declined-sender", this.player2.getName());
		plugin.getMessagesManager().sendMessage(this.player2, "island-invite-declined", this.player1.getName());
	}

	public void expired() {
		plugin.getMessagesManager().sendMessage(this.player1, "island-invite-expired-sender", this.player2.getName());
		plugin.getMessagesManager().sendMessage(this.player2, "island-invite-expired", this.player1.getName());
	}

	public Boolean isExpired() {
		long elapsed = System.currentTimeMillis() - sentTime;
		if (elapsed > plugin.getConfigManager().getInviteTimeout() * 1000) {
			return true;
		}
		return false;
	}

	public PlayerInfo getpInfo1() {
		return pInfo1;
	}

	public PlayerInfo getpInfo2() {
		return pInfo2;
	}

	public Player getPlayer1() {
		return this.player1;
	}

	public Player getPlayer2() {
		return this.player2;
	}
}
