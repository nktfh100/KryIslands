package com.nktfh100.KryIslands.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.nktfh100.KryIslands.info.Island;
import com.nktfh100.KryIslands.info.IslandInvite;
import com.nktfh100.KryIslands.info.PlayerInfo;
import com.nktfh100.KryIslands.main.KryIslands;

public class KryIslandsCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "------------------------------------------");
			sender.sendMessage("       " + ChatColor.GOLD + "" + ChatColor.BOLD + "KryIslands");

			sender.sendMessage(ChatColor.YELLOW + "/ks invite <player>" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Invite a player to build on your island");
			sender.sendMessage(ChatColor.YELLOW + "/ks acceptinvite <player>" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Accept another player's invite");
			sender.sendMessage(ChatColor.YELLOW + "/ks decline <player>" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Decline another player's invite");
			sender.sendMessage(ChatColor.YELLOW + "/ks removemember <player>" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Remove a member from your island");
			sender.sendMessage(ChatColor.YELLOW + "/ks travel" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Travel to your island");
			sender.sendMessage(ChatColor.YELLOW + "/ks travel <player>" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Travel to another player's island");
			sender.sendMessage(ChatColor.YELLOW + "/ks members" + ChatColor.WHITE + " - " + ChatColor.GOLD + "List the members of your island");
			sender.sendMessage(ChatColor.YELLOW + "/ks regenerate" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Reset your island");

			if (sender.hasPermission("KryIslands.admin")) {
				sender.sendMessage(ChatColor.YELLOW + "/ks setlobby" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Set the lobby location");
			}

			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "------------------------------------------");
			return true;
		} else if (args.length >= 1 && sender instanceof Player) {
			Player player = (Player) sender;
			KryIslands plugin = KryIslands.getInstance();
			PlayerInfo pInfo = plugin.getPlayersManager().getPlayerInfo(player);
			Island island = pInfo.getIsland();
			if (args[0].equals("setlobby")) {
				if (player.hasPermission("KryIslands.admin")) {
					Location loc = player.getLocation();
					plugin.getConfigManager().setLobby(loc);
					FileConfiguration config = plugin.getConfig();
					config.set("lobby.world", loc.getWorld().getName());
					config.set("lobby.x", loc.getX());
					config.set("lobby.y", loc.getY());
					config.set("lobby.z", loc.getZ());
					config.set("lobby.yaw", loc.getYaw());
					config.set("lobby.pitch", loc.getPitch());
					plugin.saveConfig();
					player.sendMessage(ChatColor.GREEN + "Successfully changed the lobby location");
				} else {
					plugin.getMessagesManager().sendMessage(player, "no-permission");
				}
			} else if (args[0].equals("travel")) {
				if (args.length == 1) {
					if (plugin.getIslandsManager().isIslandRegenerating(player.getUniqueId())) {
						plugin.getMessagesManager().sendMessage(player, "island-regenerating");
						return true;
					}
					plugin.getMessagesManager().sendMessage(player, "travel");
					pInfo.setVisitingIsland(null);
					player.setFallDistance(0);
					player.teleport(island.getSpawn());
					return true;
				} else {
					OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);
					if (plugin.getIslandsManager().isIslandRegenerating(targetPlayer.getUniqueId())) {
						plugin.getMessagesManager().sendMessage(player, "island-regenerating");
						return true;
					}
					if (targetPlayer.isOnline()) {
						PlayerInfo targetPlayerInfo = plugin.getPlayersManager().getPlayerByUUID(targetPlayer.getUniqueId().toString());
						Island targetIsland = targetPlayerInfo.getIsland();
						plugin.getMessagesManager().sendMessage(player, "travel-other", targetPlayer.getName());
						player.setFallDistance(0);
						player.teleport(targetIsland.getSpawn());
						pInfo.setVisitingIsland(targetIsland);
						return true;
					} else {
						if (plugin.getIslandsManager().islandExists(targetPlayer.getUniqueId())) {
							Island targetIsland = new Island(plugin.getIslandsManager().getIslandKey(targetPlayer.getUniqueId()), plugin.getIslandsManager().getIslandsWorld(),
									plugin.getIslandsManager().getDataConfig(), plugin);
							plugin.getMessagesManager().sendMessage(player, "travel-other", targetPlayer.getName());
							player.setFallDistance(0);
							player.teleport(targetIsland.getSpawn());
							pInfo.setVisitingIsland(targetIsland);
							return true;
						} else {
							plugin.getMessagesManager().sendMessage(player, "island-doesnt-exsits");
							return true;
						}
					}
				}
			} else if (args[0].equals("invite") && args.length == 2 && !args[1].equals(player.getName())) {
				Player targetPlayer = Bukkit.getPlayer(args[1]);
				if (targetPlayer == null || !targetPlayer.isOnline()) {
					plugin.getMessagesManager().sendMessage(player, "player-not-online");
					return true;
				}
				if (plugin.getInvitesManager().getInvite(targetPlayer, player) != null) {
					plugin.getMessagesManager().sendMessage(player, "already-invited", args[1]);
					return true;
				}

				plugin.getMessagesManager().sendMessage(player, "island-invite-sent", targetPlayer.getName(), plugin.getConfigManager().getInviteTimeout() + "");

				plugin.getMessagesManager().sendMessage(targetPlayer, "island-invite", player.getName(), plugin.getConfigManager().getInviteTimeout() + "");

				IslandInvite newInvite = new IslandInvite(player, targetPlayer);
				plugin.getInvitesManager().addInvite(newInvite);

			} else if (args[0].equals("acceptinvite") && args.length == 2) {
				Player targetPlayer = Bukkit.getPlayer(args[1]);
				if (targetPlayer == null || !targetPlayer.isOnline()) {
					plugin.getMessagesManager().sendMessage(player, "player-not-online");
					return true;
				}
				IslandInvite invite = plugin.getInvitesManager().getInvite(targetPlayer, player);
				if (invite == null) {
					plugin.getMessagesManager().sendMessage(player, "not-invited", args[1]);
					return true;
				}

				invite.acceptInvite();
				plugin.getInvitesManager().removeInvite(invite);

			} else if (args[0].equals("decline") && args.length == 2) {
				Player targetPlayer = Bukkit.getPlayer(args[1]);
				if (targetPlayer == null || !targetPlayer.isOnline()) {
					plugin.getMessagesManager().sendMessage(player, "player-not-online");
					return true;
				}
				IslandInvite invite = plugin.getInvitesManager().getInvite(targetPlayer, player);
				if (invite == null) {
					plugin.getMessagesManager().sendMessage(player, "not-invited", args[1]);
					return true;
				}
				invite.declineInvite();
				plugin.getInvitesManager().removeInvite(invite);

			} else if (args[0].equals("removemember") && args.length == 2 && !args[1].equals(player.getName())) {
				OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);
				if (island.hasPlayer(targetPlayer.getUniqueId())) {
					island.removeMember(targetPlayer.getUniqueId().toString());
					island.saveIsland();
					plugin.getMessagesManager().sendMessage(player, "removed-member", args[1]);
					if (targetPlayer.isOnline()) {
						PlayerInfo targetInfo = plugin.getPlayersManager().getPlayerInfo(targetPlayer.getPlayer());
						if (targetInfo.getVisitingIsland() == island) {
							targetPlayer.getPlayer().teleport(plugin.getConfigManager().getLobby());
						}
					}
					return true;
				} else {
					plugin.getMessagesManager().sendMessage(player, "not-member", args[1]);
					return true;
				}
			} else if (args[0].equals("regenerate")) {
				plugin.getIslandsManager().regenerateIsland(player);
				return true;
			} else if (args[0].equals("members")) {
				List<UUID> membersUUIDs = island.getMembers();
				String names_ = player.getName() + ", ";
				for (UUID uuid : membersUUIDs) {
					OfflinePlayer player_ = Bukkit.getOfflinePlayer(uuid);
					if (player_ != null && player_.getName() != null) {
						names_ = names_ + player_.getName() + ", ";
					}
				}
				names_ = names_.substring(0, names_.length() - 2);
				plugin.getMessagesManager().sendMessage(player, "members", names_);
				return true;
			}
		}
		return true;
	}
}
