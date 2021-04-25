package core.events;

/* *
 * 
 *  About: Listen for spawn related events from bukkit servers and,
 *  	if configured to, set players' respawn locations randomly
 * 
 *  LICENSE: AGPLv3 (https://www.gnu.org/licenses/agpl-3.0.en.html)
 *  Copyright (C) 2021  Lysergik Productions (https://github.com/LysergikProductions)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * */

import core.backend.Config;
import core.events.ChunkListener;

import java.util.*;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

public class SpawnController implements Listener {
	
	static boolean debug = Boolean.parseBoolean(Config.getValue("debug"));
	
	static double max_x; static double max_z;
	static double min_x; static double min_z;
	
	static Double config_max_x = Double.parseDouble(Config.getValue("spawn.max.X"));
	static Double config_max_z = Double.parseDouble(Config.getValue("spawn.max.Z"));
	static Double config_min_x = Double.parseDouble(Config.getValue("spawn.min.X"));
	static Double config_min_z = Double.parseDouble(Config.getValue("spawn.min.Z"));
	
	public static int sessionTotalRespawns = 0;
	
	public static ArrayList<Material> BannedSpawnFloors = new ArrayList<>(); {
		BannedSpawnFloors.addAll(Arrays.asList(
				Material.CAVE_AIR, Material.VOID_AIR, Material.WALL_TORCH));
	}
	
	public static int getRandomNumber(int min, int max) {
	    return (int) ((Math.random() * (max - min)) + min);
	}
	
	// takes a location object and gives it a random location within spawn range
	public static Location getRandomSpawn(World thisWorld, Location newSpawnLocation) {
		
		boolean valid_spawn_location = false;
		boolean open_air = Boolean.parseBoolean(Config.getValue("spawn.prevent.inside"));
		
		// if a configured value can't be parsed to Double, set to default
		if (config_max_x.isNaN()) max_x = 420.0; else max_x = config_max_x.doubleValue();
		if (config_max_z.isNaN()) max_z = 420.0; else max_z = config_max_z.doubleValue();	
		if (config_min_x.isNaN()) min_x = -420.0; else min_x = config_min_x.doubleValue();
		if (config_min_z.isNaN()) min_z = -420.0; else min_z = config_min_z.doubleValue();
		
		// get random x, z coords and check them top-down from y256 for validity
		while (!valid_spawn_location) {
			
			// get random x, z coords within range and refer to the *center* of blocks
			double tryLocation_x = Math.rint(getRandomNumber((int)min_x, (int)max_x)) + 0.5;
			double tryLocation_z = Math.rint(getRandomNumber((int)min_z, (int)max_z)) + 0.5;
			
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
						
						if (debug)
							System.out.println("Found valid respawn location on "
									+ floorBlock.getType().toString() + "!");
						
						valid_spawn_location = true;
						
						newSpawnLocation.setWorld(thisWorld);
						newSpawnLocation.setX((double)tryLocation_x);
						newSpawnLocation.setY((double)y);
						newSpawnLocation.setZ((double)tryLocation_z);
						
						break;
						
					} else if (open_air) {break;
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
		
		sessionTotalRespawns++;
		
		if (Config.getValue("spawn.prevent.burn").equals("true")) {
			if (!BannedSpawnFloors.contains(Material.LAVA)) BannedSpawnFloors.add(Material.LAVA);
			if (!BannedSpawnFloors.contains(Material.FIRE)) BannedSpawnFloors.add(Material.FIRE);
			if (!BannedSpawnFloors.contains(Material.SOUL_FIRE)) BannedSpawnFloors.add(Material.SOUL_FIRE);
		}
		
		if (Config.getValue("spawn.prevent.drown").equals("true")) {
			if (!BannedSpawnFloors.contains(Material.WATER)) BannedSpawnFloors.add(Material.WATER);
		}
		
		if (Config.getValue("spawn.prevent.pain").equals("true")) {
			if (!BannedSpawnFloors.contains(Material.CACTUS)) BannedSpawnFloors.add(Material.CACTUS);
			if (!BannedSpawnFloors.contains(Material.WITHER_ROSE)) BannedSpawnFloors.add(Material.WITHER_ROSE);
			if (!BannedSpawnFloors.contains(Material.MAGMA_BLOCK)) BannedSpawnFloors.add(Material.MAGMA_BLOCK);
		}
		
		// find then set a random spawn location if the player doesn't have a set spawn
		if (Config.getValue("spawn.random").equals("true")) {			
			if (!event.isBedSpawn() && !event.isAnchorSpawn()) {
				
				thisLocation = getRandomSpawn(thisWorld, thisLocation);
				
				if (thisLocation != null) {
					Chunk spawnChunk = thisLocation.getChunk();
					
					if (Config.getValue("spawn.repair.roof").equals("true")) ChunkListener.repairBedrockROOF(spawnChunk, event.getPlayer());
					if (Config.getValue("spawn.repair.floor").equals("true")) ChunkListener.repairBedrockFLOOR(spawnChunk, event.getPlayer());
					
					event.setRespawnLocation(thisLocation);
					while (!spawnChunk.isLoaded()) spawnChunk.load(true);
					
					if (Config.getValue("spawn.repair.roof").equals("true")) ChunkListener.repairBedrockROOF(spawnChunk, event.getPlayer());
					if (Config.getValue("spawn.repair.floor").equals("true")) ChunkListener.repairBedrockFLOOR(spawnChunk, event.getPlayer());
					
					return;
				}
			}
			System.out.println(event.getPlayer().getName() + " has a bed or anchor spawn");
		}
		System.out.println(event.getPlayer().getName() + "'s respawn event was ignored by rvas-core");
	}
	
	@EventHandler // Brand-new players spawn randomly, according to configs
	public void onJoin(PlayerJoinEvent event) {
		if (debug) System.out.println("PlayerJoinEvent triggered.");
		
		final World thisWorld = event.getPlayer().getWorld();
		Location thisLocation = thisWorld.getSpawnLocation();
		Chunk spawnChunk = thisLocation.getChunk();
		
		ChunkListener.fixEndExit(spawnChunk);
		
		if (Config.getValue("spawn.repair.roof").equals("true")) ChunkListener.repairBedrockROOF(spawnChunk, event.getPlayer());
		if (Config.getValue("spawn.repair.floor").equals("true")) ChunkListener.repairBedrockFLOOR(spawnChunk, event.getPlayer());
		
		if (Config.getValue("spawn.prevent.burn").equals("true")) {
			if (!BannedSpawnFloors.contains(Material.LAVA)) BannedSpawnFloors.add(Material.LAVA);
			if (!BannedSpawnFloors.contains(Material.FIRE)) BannedSpawnFloors.add(Material.FIRE);
			if (!BannedSpawnFloors.contains(Material.SOUL_FIRE)) BannedSpawnFloors.add(Material.SOUL_FIRE);
		}
		
		if (Config.getValue("spawn.prevent.drown").equals("true")) {
			if (!BannedSpawnFloors.contains(Material.WATER)) BannedSpawnFloors.add(Material.WATER);
		}
		
		if (Config.getValue("spawn.prevent.pain").equals("true")) {
			if (!BannedSpawnFloors.contains(Material.CACTUS)) BannedSpawnFloors.add(Material.CACTUS);
			if (!BannedSpawnFloors.contains(Material.WITHER_ROSE)) BannedSpawnFloors.add(Material.WITHER_ROSE);
			if (!BannedSpawnFloors.contains(Material.MAGMA_BLOCK)) BannedSpawnFloors.add(Material.MAGMA_BLOCK);
		}
		
		if (Config.getValue("spawn.random.join").equals("true")) {
			
			Player joiner = event.getPlayer();
			String joiner_name = joiner.getName();
			UUID joiner_id = joiner.getUniqueId();
			
			OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(joiner_id);
			boolean playedBefore = offPlayer.hasPlayedBefore();
			
			if (!playedBefore) {
				System.out.println(joiner_name + "is playing for the first time!");
				
				for (Player onlinePlayer: Bukkit.getServer().getOnlinePlayers()) {
					if (onlinePlayer.isOp()) {
						onlinePlayer.spigot().sendMessage(new TextComponent(joiner_name + " is a brand new uuid!"));
					}
				}
				
				thisLocation = getRandomSpawn(thisWorld, thisLocation);
				
				if (thisLocation != null) {
					
					thisWorld.setSpawnLocation(thisLocation);
					return;
				}
			}
		}
	}
}
