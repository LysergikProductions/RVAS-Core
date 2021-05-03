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

import core.backend.Utilities;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import org.bukkit.block.Block;
import org.bukkit.Material;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

public class BlockListener implements Listener {

	static boolean debug = Boolean.parseBoolean(Config.getValue("debug"));
	static boolean verbose = Boolean.parseBoolean(Config.getValue("verbose"));
	static boolean roofProt = Boolean.parseBoolean(Config.getValue("protect.bedrock.roof"));
	static boolean floorProt = Boolean.parseBoolean(Config.getValue("protect.bedrock.floor"));
	static boolean modeOnPlace = Boolean.parseBoolean(Config.getValue("protect.gamemode.onplace"));
	static boolean modeOnBreak = Boolean.parseBoolean(Config.getValue("protect.gamemode.onbreak"));
	
	public static int brokenBedrockCounter = 0;
	public static int placedBedrockCounter = 0;
	
	//public static ArrayList<Location> ExitPortalBlocks = new ArrayList<>();
	
	public static ArrayList<Material> BreakBanned = new ArrayList<>();
	static {
		BreakBanned.addAll(Arrays.asList(Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK,
				Material.REPEATING_COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART,
				Material.WATER, Material.LAVA, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID));
	}
	
	public static ArrayList<Material> PlacementBanned = new ArrayList<>();
	{ // TODO: add config for restricting bedrock and portal-frame placement by adding to this array accordingly
		PlacementBanned.addAll(Arrays.asList(Material.BARRIER, Material.COMMAND_BLOCK,
				Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART,
				Material.WATER, Material.LAVA, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID));
	}
	
	public static ArrayList<Material> LagMats = new ArrayList<>();
	static {
		LagMats.addAll(Arrays.asList(Material.REDSTONE, Material.REDSTONE_WIRE, Material.REDSTONE_BLOCK,
				Material.REDSTONE_TORCH, Material.REDSTONE_WALL_TORCH, Material.ACTIVATOR_RAIL, Material.POWERED_RAIL,
				Material.LEVER, Material.PISTON, Material.STICKY_PISTON, Material.REDSTONE_LAMP, Material.GLOWSTONE,
				Material.OBSERVER, Material.HOPPER, Material.DROPPER, Material.REPEATER, Material.COMPARATOR,
				Material.DISPENSER, Material.GRAVEL, Material.ARMOR_STAND, Material.TRIPWIRE_HOOK, Material.TRIPWIRE));
	}
	
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

			String breaker_name = breaker.getName();
			Material blockType = block.getType();
			Environment dimension = block.getWorld().getEnvironment();
			
			TextComponent cancelPos = new TextComponent(
					breaker_name + "'s BlockBreakEvent was cancelled: " + blockType);
			
			if (!breaker.isOp() || modeOnBreak) breaker.setGameMode(GameMode.SURVIVAL);
			
			if (BreakBanned.contains(blockType)) {

				System.out.println("WARN " + breaker_name + " tried to break a protected admin block!");
				if (debug && verbose) Bukkit.spigot().broadcast(cancelPos);

				event.setCancelled(true);
				
			// do things if block == bedrock
			} else if (blockType.equals(Material.BEDROCK)) {
				
				// protect bedrock floor
				if (y < 1 && floorProt) {

					System.out.println("WARN " + breaker_name + " tried to break a protected floor block!");
					if (debug && verbose) Bukkit.spigot().broadcast(cancelPos);

					event.setCancelled(true);
					return;
					
				// protect nether roof	
				} else if (y == 127 && roofProt &&
						dimension.equals(Environment.NETHER)) {

					System.out.println("WARN " + breaker_name + " tried to break a protected roof block!");
					if (debug && verbose) Bukkit.spigot().broadcast(cancelPos);

					event.setCancelled(true);
					return;
				
				// protect exit portal in the end
				} else if (dimension.equals(Environment.THE_END) &&
						y == 63 || y == 64) {
					
					if (x < 4 && x > -4) {
						if (z < 4 && z > -4) {
							
							System.out.println("WARN " + breaker_name + " tried to break a protected exit portal block!");
							if (debug && verbose) Bukkit.spigot().broadcast(cancelPos);

							event.setCancelled(true);
							return;
						}
					}
				}
				
				if (debug && !verbose) Bukkit.spigot().broadcast(
						new TextComponent(breaker_name + " just broke BEDROCK!"));
				brokenBedrockCounter++;
				
			// protect natural The_End entry and exit portals
			} else if (blockType.equals(Material.END_PORTAL)) {
				
				if (dimension.equals(Environment.THE_END) && y ==64) {
					
					if (x < 4 && x > -4) {
						if (z < 4 && z > -4) {
							
							event.setCancelled(true);
							if (debug && !verbose) Bukkit.spigot().broadcast(cancelPos);
						}
					}
				}
			}
			
			//long endTime = System.nanoTime();
			//long duration = (endTime - startTime);
			//System.out.println("BreakTime: " + new DecimalFormat("#.###").format((double)duration/1000000.0) + " ms");
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlace(BlockPlaceEvent event) {
		
		long startTime = System.nanoTime();		
		if (debug) System.out.println("onPlace triggered.." + startTime);
		
		Player placer = event.getPlayer();
		if (PlayerMeta.isAdmin(placer)) return;
		String placer_name = placer.getName();
		
		Block block = event.getBlockPlaced();
		Location block_loc = block.getLocation();
		Environment dimension = block_loc.getWorld().getEnvironment();
		String env;

		if (dimension.equals(Environment.NORMAL)) env = "overworld";
		else if (dimension.equals(Environment.NETHER)) env = "the_nether";
		else if (dimension.equals(Environment.THE_END)) env = "the_end";
		else env = null;

		Material blockType = block.getType();
		String mat = blockType.toString();

		if (!placer.isOp() || modeOnPlace) placer.setGameMode(GameMode.SURVIVAL);

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

		// Watchdog for potential lag machines
		if (LagMats.contains(blockType) || mat.contains("MINECART") || mat.contains("DOOR")) {
			int counter = 0;

			for (Material thisMat: LagMats) {
				if (thisMat != Material.GRAVEL) {
					counter += Utilities.blockCounter(block.getChunk(), thisMat);
				}
			}

			TextComponent warn = new TextComponent("WARN "); warn.setBold(true);
			warn.setColor(ChatColor.RED);

			TextComponent msg = new TextComponent("Potential lag-machine at " +
					block.getX() + ", " + block.getY() + ", " + block.getZ() + " in " + dimension +
					" by " + placer_name + " with UUID: " + placer.getUniqueId());

			String cmd = "/execute in " + env + " run tp @s " +
					block.getX() + " " + block.getY() + " " + block.getZ();

			msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));

			if (counter > 256) {
				Utilities.notifyOps(new TextComponent(warn, msg));
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
				
				if (debug) Bukkit.spigot().broadcast(
						new TextComponent(placer_name + "'s BlockPlaceEvent was cancelled."));
				return;
			} else if (blockType.equals(Material.BEDROCK)) {
				placedBedrockCounter++;
				System.out.println("WARN: " + placer_name + " just placed bedrock at " + block_loc);
			}
		}
		
		if (debug && verbose) {
			long endTime = System.nanoTime();
			long duration = (endTime - startTime);
			
			placer.spigot().sendMessage(new TextComponent(
					"PlaceTime: " + new DecimalFormat("#.###").format((double)duration/1000000.0) + " ms"));
		}
	}
	
	public static boolean updateConfigs() {
		
		try {
			debug = Boolean.parseBoolean(Config.getValue("debug"));
			verbose = Boolean.parseBoolean(Config.getValue("verbose"));
			roofProt = Boolean.parseBoolean(Config.getValue("protect.bedrock.roof"));
			floorProt = Boolean.parseBoolean(Config.getValue("protect.bedrock.floor"));
			modeOnPlace = Boolean.parseBoolean(Config.getValue("protect.gamemode.onplace"));
			modeOnBreak = Boolean.parseBoolean(Config.getValue("protect.gamemode.onbreak"));
			
			return true;
			
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
}
