package com.nktfh100.KryIslands.main;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.nktfh100.KryIslands.commands.KryIslandsCommand;
import com.nktfh100.KryIslands.commands.KryIslandsCommandTab;
import com.nktfh100.KryIslands.events.BlockBreak;
import com.nktfh100.KryIslands.events.BlockPlace;
import com.nktfh100.KryIslands.events.PlayerDamage;
import com.nktfh100.KryIslands.events.PlayerEnterPortal;
import com.nktfh100.KryIslands.events.PlayerInteract;
import com.nktfh100.KryIslands.events.PlayerRespawn;
import com.nktfh100.KryIslands.info.Island;
import com.nktfh100.KryIslands.info.PlayerInfo;
import com.nktfh100.KryIslands.managers.ConfigManager;
import com.nktfh100.KryIslands.managers.InvitesManager;
import com.nktfh100.KryIslands.managers.IslandsManager;
import com.nktfh100.KryIslands.managers.MessagesManager;
import com.nktfh100.KryIslands.managers.PlayersManager;
import com.nktfh100.KryIslands.utils.SkyblockGenerator;

public class KryIslands extends JavaPlugin {

	private static KryIslands instance;

	private ChunkGenerator chunkGenerator;

	private PlayersManager playersManager;
	private ConfigManager configManager;
	private MessagesManager messagesManager;
	private IslandsManager islandsManager;
	private InvitesManager invitesManager;

	public KryIslands() {
		instance = this;
	}

	@Override
	public void onLoad() {
		chunkGenerator = new SkyblockGenerator();
	}

	@Override
	public void onEnable() {
		configManager = new ConfigManager(this);
		islandsManager = new IslandsManager(this);
		if (!this.isEnabled()) {
			return;
		}
		messagesManager = new MessagesManager(this);
		invitesManager = new InvitesManager(this);
		new BukkitRunnable() { // This since onEnable is called before the worlds load
			@Override
			public void run() {
				islandsManager.loadWorld();
				configManager.loadConfig();
				messagesManager.loadAll();
				islandsManager.loadData();
				startBorderTask();
			}
		}.runTaskLater(this, 2L);

		playersManager = new PlayersManager(this);
		getServer().getPluginManager().registerEvents(playersManager, this);
		getServer().getPluginManager().registerEvents(new PlayerDamage(), this);
		getServer().getPluginManager().registerEvents(new BlockPlace(), this);
		getServer().getPluginManager().registerEvents(new BlockBreak(), this);
		getServer().getPluginManager().registerEvents(new PlayerRespawn(), this);
		getServer().getPluginManager().registerEvents(new PlayerEnterPortal(), this);
		getServer().getPluginManager().registerEvents(new PlayerInteract(), this);

		this.getCommand("ks").setExecutor(new KryIslandsCommand());
		this.getCommand("ks").setTabCompleter(new KryIslandsCommandTab());

	}

	// To not let the players get out of their island's bounds
	public void startBorderTask() {
		KryIslands plugin = this;
		World world = this.getIslandsManager().getIslandsWorld();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!plugin.isEnabled()) {
					this.cancel();
					return;
				}
				for (PlayerInfo pInfo : plugin.getPlayersManager().getPlayers()) {
					if (pInfo.getPlayer().getWorld() != world) {
						continue;
					}
					Location pLoc = pInfo.getPlayer().getLocation();
					Island island = pInfo.getIsland();
					if (island == null && pInfo.getVisitingIsland() == null) {
						continue;
					}
					if (pInfo.getVisitingIsland() != null) {
						island = pInfo.getVisitingIsland();
					}
					if (!island.inBounds(pLoc)) {
						double xLoc = pLoc.getX();
						double zLoc = pLoc.getZ();
						double yLoc = pLoc.getY();

						if (xLoc <= island.getMinX())
							xLoc = island.getMaxX() - 3;
						else if (xLoc >= island.getMaxX())
							xLoc = island.getMinX() + 3;
						if (zLoc <= island.getMinZ())
							zLoc = island.getMaxZ() - 3;
						else if (zLoc >= island.getMaxZ())
							zLoc = island.getMinZ() + 3;
						// Launch the player back inside the island's bounds
						double x = xLoc - pLoc.getX();
						double y = yLoc - pLoc.getY();
						double z = zLoc - pLoc.getZ();
						pInfo.getPlayer().setVelocity(new Vector(x, y, z).normalize().multiply(1.0));
						pInfo.getPlayer().sendMessage(plugin.getMessagesManager().getMsg("reached-border"));
					}
				}
			}
		}.runTaskTimer(plugin, 60L, 5L);
	}

	@Override
	public void onDisable() {
		if (islandsManager != null) {
			islandsManager.saveData();
		}
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, @Nullable String id) {
		return this.chunkGenerator;
	}

	public static KryIslands getInstance() {
		return instance;
	}

	public ConfigManager getConfigManager() {
		return this.configManager;
	}

	public MessagesManager getMessagesManager() {
		return this.messagesManager;
	}

	public IslandsManager getIslandsManager() {
		return islandsManager;
	}

	public PlayersManager getPlayersManager() {
		return playersManager;
	}

	public InvitesManager getInvitesManager() {
		return invitesManager;
	}

}
