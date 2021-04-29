package com.nktfh100.KryIslands.managers;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.nktfh100.KryIslands.main.KryIslands;

public class MessagesManager {

	private KryIslands plugin;

	private HashMap<String, String> msgs = new HashMap<String, String>();

	public MessagesManager(KryIslands plugin) {
		this.plugin = plugin;
	}

	public void loadAll() {
		File msgsConfigFIle = new File(this.plugin.getDataFolder(), "messages.yml");
		if (!msgsConfigFIle.exists()) {
			try {
				this.plugin.saveResource("messages.yml", false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String prefix = plugin.getConfigManager().getPrefix();
		YamlConfiguration msgsConfig = YamlConfiguration.loadConfiguration(msgsConfigFIle);
		try {
			this.msgs.clear();

			ConfigurationSection gameMsgsSC = msgsConfig.getConfigurationSection("msgs");
			Set<String> gameMsgsKeys = gameMsgsSC.getKeys(false);
			for (String key : gameMsgsKeys) {
				String msg_ = gameMsgsSC.getString(key);
				msg_ = msg_.replaceAll("%prefix%", prefix);
				this.msgs.put(key, ChatColor.translateAlternateColorCodes('&', msg_));
			}
		} catch (Exception e) {
			e.printStackTrace();
			plugin.getLogger().warning("Something is wrong with your messages.yml file!");
		}
	}

	public void sendMessage(Player player, String key, String... extras) {
		if (player != null && player.isOnline() && key != null && !key.isEmpty()) {
			String msg = this.getMsg(key, extras);
			for (String line : msg.split("/n")) {
				player.sendMessage(line);
			}
		}
	}

	private String getMsg(String key, String... extras) {
		if (this.msgs == null) {
			return "";
		}
		String output = this.msgs.get(key);
		if (output == null) {
			this.plugin.getLogger().warning("Message '" + key + "' is missing from your messages.yml file!");
			return "";
		}
		int i = 0;
		for (String extra : extras) {
			if (i == 0) {
				output = output.replaceAll("%value%", extra);
			} else {
				output = output.replaceAll("%value" + i + "%", extra);
			}
			i++;
		}
		return output;
	}

	public String getMsg(String key) {
		String output = getMsg(key, "");
		return output;
	}

}
