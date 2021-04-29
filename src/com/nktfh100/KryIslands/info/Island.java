package com.nktfh100.KryIslands.info;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.nktfh100.KryIslands.main.KryIslands;

public class Island {

	private KryIslands plugin;
	private int islandKey;
	private World world;
	private FileConfiguration data;
	private Location p1;
	private Location p2;
	private String owner; // UUID
	private List<String> members; // UUID
	private String schematic;
	private Location spawn;

	private int minX;
	private int maxX;
	private int minZ;
	private int maxZ;

	public Island(int islandKey, World world, FileConfiguration data, KryIslands plugin) {
		this.islandKey = islandKey;
		this.data = data;
		this.world = world;
		this.plugin = plugin;
		String keyPrefix = "islands." + islandKey;
		if (data.isSet(keyPrefix)) {
			this.p1 = new Location(world, data.getInt(keyPrefix + ".p1.x"), data.getInt(keyPrefix + ".p1.y"), data.getInt(keyPrefix + ".p1.z"));
			this.p2 = new Location(world, data.getInt(keyPrefix + ".p2.x"), data.getInt(keyPrefix + ".p2.y"), data.getInt(keyPrefix + ".p2.z"));
			this.spawn = new Location(world, data.getInt(keyPrefix + ".spawn.x"), data.getInt(keyPrefix + ".spawn.y"), data.getInt(keyPrefix + ".spawn.z"));
			this.owner = data.getString(keyPrefix + ".owner");
			this.members = data.getStringList(keyPrefix + ".members");
		}
		this.updateMinMax();
	}

	public Island(Location p1, Location p2, Location spawn, int islandKey, Player owner, World world, String schem, FileConfiguration data, KryIslands plugin) {
		this.p1 = p1;
		this.p2 = p2;
		this.spawn = spawn;
		this.islandKey = islandKey;
		this.owner = owner.getUniqueId().toString();
		this.world = world;
		this.plugin = plugin;
		this.data = data;
		this.members = new ArrayList<String>();
		this.schematic = schem;
		this.updateMinMax();
	}

	private void updateMinMax() {
		if (this.p1 != null && this.p2 != null) {
			this.minX = Math.min(this.p1.getBlockX(), this.p2.getBlockX());
			this.minZ = Math.min(this.p1.getBlockZ(), this.p2.getBlockZ());
			this.maxX = Math.max(this.p1.getBlockX(), this.p2.getBlockX());
			this.maxZ = Math.max(this.p1.getBlockZ(), this.p2.getBlockZ());
		}
	}

	public Location getP1() {
		return this.p1;
	}

	public Location getP2() {
		return this.p2;
	}

	public Boolean inBounds(Location loc) {
		if (loc.getX() >= this.p1.getX() && this.p2.getX() >= loc.getX() && loc.getZ() >= this.p1.getZ() && this.p2.getZ() >= loc.getZ()) {
			return true;
		}
		return false;
	}

	public void saveIsland() {
		String keyPrefix = "islands." + String.valueOf(this.islandKey);
		if (this.p1 != null) {
			this.data.set(keyPrefix + ".p1.x", this.p1.getBlockX());
			this.data.set(keyPrefix + ".p1.y", this.p1.getBlockY());
			this.data.set(keyPrefix + ".p1.z", this.p1.getBlockZ());
		}
		if (this.p2 != null) {
			this.data.set(keyPrefix + ".p2.x", this.p2.getBlockX());
			this.data.set(keyPrefix + ".p2.y", this.p2.getBlockY());
			this.data.set(keyPrefix + ".p2.z", this.p2.getBlockZ());
		}
		if (this.spawn != null) {
			this.data.set(keyPrefix + ".spawn.x", this.spawn.getBlockX());
			this.data.set(keyPrefix + ".spawn.y", this.spawn.getBlockY());
			this.data.set(keyPrefix + ".spawn.z", this.spawn.getBlockZ());
		}
		this.data.set(keyPrefix + ".owner", this.owner);
		this.data.set(keyPrefix + ".members", this.members);
		try {
			this.plugin.getIslandsManager().getDataConfig().save(this.plugin.getDataFolder() + File.separator + "data.yml");
		} catch (IOException e) {
			this.plugin.getLogger().warning("Could not save data.yml");
		}
	}

	public boolean hasPlayer(Player p) {
		if (p != null) {
			String playerUUID = p.getUniqueId().toString();
			if (this.owner.equalsIgnoreCase(playerUUID) || this.members.contains(playerUUID)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasPlayer(UUID UUID) {
		String playerUUID = UUID.toString();
		if (this.owner.equalsIgnoreCase(playerUUID) || this.members.contains(playerUUID)) {
			return true;
		}
		return false;
	}

	public UUID getOwner() {
		return UUID.fromString(this.owner);
	}

	public List<UUID> getMembers() {
		List<UUID> out = new ArrayList<UUID>();
		for (int i = 0; i < members.size(); i++) {
			if (members.get(i) != null) {
				out.add(UUID.fromString(members.get(i)));
			}
		}
		return out;
	}

	public void addMember(Player p) {
		this.members.add(p.getUniqueId().toString());
	}

	public void removeMember(String uuid) {
		this.members.remove(uuid);
	}

	public int getKey() {
		return this.islandKey;
	}

	public Location getSpawn() {
		return this.spawn;
	}

	public void setSpawn(Location loc) {
		this.spawn = loc;
	}

	public String getSchematic() {
		return this.schematic;
	}

	public void setSchematic(String to) {
		this.schematic = to;
	}

	public int getMinX() {
		return this.minX;
	}

	public int getMaxX() {
		return this.maxX;
	}

	public int getMinZ() {
		return this.minZ;
	}

	public int getMaxZ() {
		return this.maxZ;
	}
}