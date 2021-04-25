package core.events;

/* *
 * 
 *  About: Use block-related events to enforce
 *  	configured restrictions in RVAS-core
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
import core.backend.PlayerMeta;

import java.util.*;
import java.text.DecimalFormat;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.Material;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

public class BlockListener implements Listener {
	
	// do these 4 need to be static? do they update with `Config.load()` from `/admin reload` ?
	static String roofProt = Config.getValue("protect.bedrock.roof");
	static String floorProt = Config.getValue("protect.bedrock.floor");
	static boolean debug = Boolean.parseBoolean(Config.getValue("debug"));
	static boolean devesp = Boolean.parseBoolean(Config.getValue("devesp"));
	
	public static int brokenBedrockCounter = 0;
	public static int placedBedrockCounter = 0;
	
	public static ArrayList<Location> ExitPortalBlocks = new ArrayList<>();
	
	public static ArrayList<Material> BreakBanned = new ArrayList<>();
	{
		BreakBanned.addAll(Arrays.asList(Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK,
				Material.REPEATING_COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART,
				Material.WATER, Material.LAVA, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID));
	}
	
	public static ArrayList<Material> PlacementBanned = new ArrayList<>();
	{
		PlacementBanned.addAll(Arrays.asList(Material.BARRIER, Material.COMMAND_BLOCK,
				Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART,
				Material.WATER, Material.LAVA, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID));
	}
	
	public static ArrayList<Material> LagMats = new ArrayList<>();
	{
		LagMats.addAll(Arrays.asList(Material.REDSTONE, Material.REDSTONE_WIRE, Material.REDSTONE_BLOCK,
				Material.REDSTONE_TORCH, Material.REDSTONE_WALL_TORCH, Material.ACTIVATOR_RAIL, Material.POWERED_RAIL,
				Material.LEVER, Material.PISTON, Material.STICKY_PISTON, Material.REDSTONE_LAMP, Material.GLOWSTONE,
				Material.OBSERVER, Material.HOPPER, Material.DROPPER, Material.REPEATER, Material.COMPARATOR,
				Material.DISPENSER, Material.GRAVEL, Material.ARMOR_STAND, Material.TRIPWIRE_HOOK, Material.TRIPWIRE));
	}
	
	static Random r = new Random();
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		
		//long startTime = System.nanoTime();		
		Player breaker = event.getPlayer();
		
		// prevent creative players from breaking certain blocks but completely ignore admin account
		if (!PlayerMeta.isAdmin(breaker)) {
			
			Block block = event.getBlock();
			Location block_loc = block.getLocation();
			
			int x = (int)block_loc.getX();
			int y = (int)block_loc.getY();
			int z = (int)block_loc.getZ();
			
			Material blockType = block.getType();
			Environment dimension = block.getWorld().getEnvironment();
			
			GameMode mode = breaker.getGameMode();		
			String breaker_name = breaker.getName();
			
			TextComponent cancelPos = new TextComponent(
					breaker_name + "'s BlockBreakEvent was cancelled: "
					+ blockType.toString());
			
			if (!breaker.isOp()) breaker.setGameMode(GameMode.SURVIVAL);			
			
			if (BreakBanned.contains(blockType)) {
				
				event.setCancelled(true);
				
				if (debug && !devesp) Bukkit.spigot().broadcast(
						new TextComponent(breaker_name +
								"'s BlockBreakEvent was cancelled: "
								+ blockType.toString()));
				return;
				
			// do things if block == bedrock
			} else if (blockType.equals(Material.BEDROCK)) {
				
				// protect bedrock floor
				if (y < 1 && floorProt.equals("true")) {
					
					event.setCancelled(true);					
					if (debug && !devesp) Bukkit.spigot().broadcast(cancelPos);
					return;
					
				// protect nether roof	
				} else if (y == 127 && roofProt.equals("true") &&
						dimension.equals(Environment.NETHER)) {
					
					event.setCancelled(true);
					if (debug && !devesp) Bukkit.spigot().broadcast(cancelPos);
					return;
				
				// protect exit portal in the end
				} else if (dimension.equals(Environment.THE_END) &&
						y == 63 || y == 64) {
					
					if (x < 4 && x > -4) {
						if (z < 4 && z > -4) {
							
							event.setCancelled(true);
							if (debug && !devesp) Bukkit.spigot().broadcast(cancelPos);
							return;
						}
					}
				}
				
				if (debug && !devesp) Bukkit.spigot().broadcast(
						new TextComponent(breaker_name + " just broke BEDROCK!"));
				brokenBedrockCounter++;
				
			// protect natural The_End entry and exit portals
			} else if (blockType.equals(Material.END_PORTAL)) {
				
				if (dimension.equals(Environment.THE_END) && y ==64) {
					
					if (x < 4 && x > -4) {
						if (z < 4 && z > -4) {
							
							event.setCancelled(true);
							if (debug && !devesp) Bukkit.spigot().broadcast(cancelPos);
							return;
						}
					}
				}
			}
			
			//long endTime = System.nanoTime();
			//long duration = (endTime - startTime);
			//System.out.println("BreakTime: " + new DecimalFormat("#.###").format((double)duration/1000000.0) + " ms");
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onPlace(BlockPlaceEvent event) {
		
		long startTime = System.nanoTime();		
		if (debug) System.out.println("onPlace triggered.." + startTime);
		
		Player placer = event.getPlayer();
		if (PlayerMeta.isAdmin(placer)) return;
		
		Block block = event.getBlockPlaced();
		Location block_loc = block.getLocation();
		GameMode mode = placer.getGameMode();		
		String placer_name = placer.getName();
		
		Material blockType = block.getType();
		String mat = blockType.toString();
		
		// prevent lag-prisoners from placing things that can cause lag
		if (PlayerMeta.isPrisoner(placer)) {

			if (LagMats.contains(blockType)) {event.setCancelled(true);
				return;
			} else if (
					mat.endsWith("SAND") ||
					mat.contains("POWDER") ||
					mat.contains("BUTTON") ||
					mat.contains("PRESSURE_PLATE") ||
					mat.contains("MINECART") ||
					mat.contains("DOOR")
					) {
				
				event.setCancelled(true);
				return;
			}
		}
		
		// for anti-rogue-op meta; cannot place shulker boxes in creative mode
		if (mat.contains("SHULKER_BOX")) {
			if (!placer.getGameMode().equals(GameMode.SURVIVAL)) {
					
				event.setCancelled(true);
			}
		}
		
		// anti roof-placement
		if (Config.getValue("protect.roof.noplacement").equals("true")) {
			if(block_loc.getY() > 127 && block_loc.getWorld().getName().endsWith("the_nether")) {
				
				event.setCancelled(true);
			}
		}
		
		// do nothing if user is placing an ender eye into an end portal frame
		if (!event.getItemInHand().getType().equals(Material.ENDER_EYE)
				&& Config.getValue("protect.banned.place").equals("true")) {
			
			// prevent all players from placing blocks totally unobtainable in survival mode, but ignore admin account					
			if (Config.getValue("protect.banned.place.ops").equals("false") && placer.isOp()) {
				return;
			}
			
			if (PlacementBanned.contains(blockType)) {
				event.setCancelled(true);
				
				if (debug && !devesp) Bukkit.spigot().broadcast(
						new TextComponent(placer_name + "'s BlockPlaceEvent was cancelled."));
				return;
			}
		}
		
		placedBedrockCounter++;
		
		if (debug && !devesp) {
			long endTime = System.nanoTime();
			long duration = (endTime - startTime);
			
			placer.spigot().sendMessage(new TextComponent(
					"PlaceTime: " + new DecimalFormat("#.###").format((double)duration/1000000.0) + " ms"));
		}
	}
	
	// this occurs after onPlace because of EventPriority
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void noGhost(BlockPlaceEvent event) {
		
		Block blockToPlace = event.getBlockPlaced();		
		blockToPlace.getState().update(false, true);
	}
	
	public static boolean updateConfigs() {
		
		try {
			roofProt = Config.getValue("protect.bedrock.roof");
			floorProt = Config.getValue("protect.bedrock.floor");
			debug = Boolean.parseBoolean(Config.getValue("debug"));
			devesp = Boolean.parseBoolean(Config.getValue("devesp"));
			
			return true;
			
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
}
