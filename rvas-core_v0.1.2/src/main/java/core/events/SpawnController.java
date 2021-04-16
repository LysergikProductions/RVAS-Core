package core.events;

import java.util.*;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.block.Block;

import core.backend.Config;

public class SpawnController implements Listener {
	
	static double max_x; static double max_z;
	static double min_x; static double min_z;
	
	static Double config_max_x = Double.parseDouble(Config.getValue("spawn.max.X"));
	static Double config_max_z = Double.parseDouble(Config.getValue("spawn.max.Z"));
	static Double config_min_x = Double.parseDouble(Config.getValue("spawn.min.X"));
	static Double config_min_z = Double.parseDouble(Config.getValue("spawn.min.Z"));
	
	public static ArrayList<Material> BannedSpawnFloors = new ArrayList<>(); {
		BannedSpawnFloors.addAll(Arrays.asList(Material.AIR));
	}
	
	public static int getRandomNumber(int min, int max) {
	    return (int) ((Math.random() * (max - min)) + min);
	}
	
	// takes a location object and gives it a random location within spawn range
	public static Location getRandomSpawn(World thisWorld, Location newSpawnLocation) {
		
		boolean valid_spawn_location = false;
		
		// if a configured value can't be parsed to Double, set to default
		if (config_max_x.isNaN()) max_x = 420.0; else max_x = config_max_x.doubleValue();
		if (config_max_z.isNaN()) max_z = 420.0; else max_z = config_max_z.doubleValue();	
		if (config_min_x.isNaN()) min_x = -420.0; else min_x = config_min_x.doubleValue();
		if (config_min_z.isNaN()) min_z = -420.0; else min_z = config_min_z.doubleValue();
		
		// get random x, z coords and check them top-down from y256 for validity
		while (!valid_spawn_location) {
			
			double tryLocation_x = getRandomNumber((int)min_x, (int)max_x);
			double tryLocation_z = getRandomNumber((int)min_z, (int)max_z);
			
			System.out.println("RVAS: Checking coords for respawn: " + tryLocation_x + ", " + tryLocation_z);
			
			int y = 257;
			while (y > 1) {
				
				Location headLoc = new Location(thisWorld, tryLocation_x, (double)y, tryLocation_z);
				Location legsLoc = new Location(thisWorld, tryLocation_x, (double)y-1, tryLocation_z);
				Location floorLoc = new Location(thisWorld, tryLocation_x, (double)y-2, tryLocation_z);
				
				Block headBlock = headLoc.getBlock();
				Block legsBlock = legsLoc.getBlock();
				Block floorBlock = floorLoc.getBlock();
				
				y--;
				
				if (!headBlock.getType().equals(Material.AIR) || !legsBlock.getType().equals(Material.AIR)) {
					continue;
					
				} else if (!floorBlock.getType().equals(Material.AIR)) {
					
					// potential valid spawn, check for unwanted spawn surfaces	
					if (!BannedSpawnFloors.contains(floorBlock.getType())) {
						
						if (Config.getValue("debug").equals("true")) System.out.println("Found valid respawn location!");
						
						valid_spawn_location = true;
						
						newSpawnLocation.setWorld(thisWorld);
						newSpawnLocation.setX((double)tryLocation_x);
						newSpawnLocation.setY((double)y);
						newSpawnLocation.setZ((double)tryLocation_z);
						
					} else continue;
				}
			}
		}
		return newSpawnLocation;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent event) {
		
		final World thisWorld = event.getRespawnLocation().getWorld();
		Location thisLocation = event.getRespawnLocation();
		
		if (Config.getValue("spawn.ignore.lava").equals("true")) {
			BannedSpawnFloors.add(Material.LAVA);
		}
		
		if (Config.getValue("spawn.ignore.water").equals("true")) {
			BannedSpawnFloors.add(Material.WATER);
		}
		
		// find then set a random spawn location if the player doesn't have a set spawn
		if (Config.getValue("spawn.random").equals("true")) {			
			if (!event.isBedSpawn() && !event.isAnchorSpawn()) {
				
				thisLocation = getRandomSpawn(thisWorld, thisLocation);
				
				if (thisLocation != null) {
					
					event.setRespawnLocation(thisLocation);
					return;
				}
			}
			System.out.println(event.getPlayer().getName() + " has a bed or anchor spawn");
		}
		System.out.println(event.getPlayer().getName() + "'s respawn event was ignored by rvas-core");
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		System.out.println("PlayerJoinEvent triggered.");
		
		final World thisWorld = event.getPlayer().getWorld();
		Location thisLocation = thisWorld.getSpawnLocation();
		
		if (Config.getValue("spawn.random.join").equals("true")) {
			
			Player joiner = event.getPlayer();
			String joiner_name = joiner.getName();
			UUID joiner_id = joiner.getUniqueId();
			
			OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(joiner_id);
			boolean playedBefore = offPlayer.hasPlayedBefore();
			
			if (!playedBefore) {
				System.out.println(joiner_name + "is playing for the first time!");
				
				thisLocation = getRandomSpawn(thisWorld, thisLocation);
				
				if (thisLocation != null) {
					
					thisWorld.setSpawnLocation(thisLocation);
					return;
				}
			}
		}
	}
}
