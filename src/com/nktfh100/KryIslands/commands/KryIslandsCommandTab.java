package com.nktfh100.KryIslands.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class KryIslandsCommandTab implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> COMMANDS = new ArrayList<>();
		int arg = 0;
		if (args.length == 1) {
			arg = 0;
			COMMANDS.add("invite");
			COMMANDS.add("acceptinvite");
			COMMANDS.add("declineinvite");
			COMMANDS.add("travel");
			COMMANDS.add("members");
			COMMANDS.add("regenerate");
			COMMANDS.add("removemember");
			if (sender.hasPermission("KryIslands.admin")) {
				COMMANDS.add("setlobby");
			}
		} else if (args.length == 2) {
			arg = 1;
			for (Player player : Bukkit.getOnlinePlayers()) {
				COMMANDS.add(player.getName());
			}
		}

		final List<String> completions = new ArrayList<>();
		StringUtil.copyPartialMatches(args[arg], COMMANDS, completions);

		Collections.sort(completions);
		return completions;
	}

}
