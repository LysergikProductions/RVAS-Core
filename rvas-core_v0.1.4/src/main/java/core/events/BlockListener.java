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

import java.util.*;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import core.backend.Config;
import core.backend.PlayerMeta;

public class BlockListener implements Listener {
	
	static Random r = new Random();
	
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
	
	// also check for containing keyword "BUTTON" or "MINECART" or "PRESSURE_PLATE" or "FAN" or "DOOR" or "POWDER" or endsWith "SAND"
	public static ArrayList<Material> LagMats = new ArrayList<>();
	{
		LagMats.addAll(Arrays.asList(Material.REDSTONE, Material.REDSTONE_WIRE, Material.REDSTONE_BLOCK,
				Material.REDSTONE_TORCH, Material.REDSTONE_WALL_TORCH, Material.ACTIVATOR_RAIL, Material.POWERED_RAIL,
				Material.LEVER, Material.PISTON, Material.STICKY_PISTON, Material.REDSTONE_LAMP, Material.GLOWSTONE,
				Material.OBSERVER, Material.HOPPER, Material.DROPPER, Material.REPEATER, Material.COMPARATOR,
				Material.DISPENSER, Material.GRAVEL, Material.ARMOR_STAND, Material.TRIPWIRE_HOOK, Material.TRIPWIRE));
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		
		// get configs
		String roofProt = Config.getValue("protect.bedrock.roof");
		String floorProt = Config.getValue("protect.bedrock.floor");
		String debug = Config.getValue("debug");
		String devesp = Config.getValue("devesp");
		
		// get commonly referenced data
		Block block = event.getBlock();
		Player breaker = event.getPlayer();
		GameMode mode = breaker.getGameMode();		
		String breaker_name = breaker.getName();
		
		// prevent creative players from breaking certain blocks but completely ignore admin account
		if (!PlayerMeta.isAdmin(breaker)) {
			if (!breaker.isOp()) {
				
				breaker.setGameMode(GameMode.SURVIVAL);
			}
			
			if (BreakBanned.contains(block.getType())) {
				
				event.setCancelled(true);
				
				if (debug.equals("true") && devesp.equals("false")) {
					Bukkit.spigot().broadcast(new TextComponent(breaker_name + "'s BlockBreakEvent was cancelled."));
				}
				
			// do things if block == bedrock
			} else if (block.getType().equals(Material.BEDROCK)) {
				if (debug.equals("true") && devesp.equals("false")) {
					Bukkit.spigot().broadcast(new TextComponent(breaker_name + " just broke a bedrock block!"));
				}
				
				// protect bedrock floor
				if (block.getLocation().getY() < 1 && floorProt.equals("true")) {
					
					event.setCancelled(true);

					if (debug.equals("true") && devesp.equals("false")) {
						Bukkit.spigot().broadcast(new TextComponent(breaker_name + "'s BlockBreakEvent was cancelled."));
					}
					
				// protect nether roof	
				} else if (block.getWorld().getEnvironment().equals(Environment.NETHER) && block.getLocation().getY() == 127 && roofProt.equals("true")) {
					
					event.setCancelled(true);

					if (debug.equals("true") && devesp.equals("false")) {
						Bukkit.spigot().broadcast(new TextComponent(breaker_name + "'s BlockBreakEvent was cancelled."));
					}
				} else if (block.getWorld().getEnvironment().equals(Environment.THE_END) && block.getLocation().getY() == 64) {
					
					if (block.getLocation().getX() == 64) {
						
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		
		// get configs
		String debug = Config.getValue("debug");
		String devesp = Config.getValue("devesp");
		
		if (debug.equals("true") && devesp.equals("false")) {
			Bukkit.spigot().broadcast(new TextComponent("BlockPlaceEvent triggered."));
		}
		
		// get commonly referenced data
		Block block = event.getBlockPlaced();
		Player placer = event.getPlayer();
		GameMode mode = placer.getGameMode();		
		String placer_name = placer.getName();
		
		// Make game unplayable for laggers
		if (PlayerMeta.isLagfag(placer)) {
			
			if (LagMats.contains(block.getType())) {
				event.setCancelled(true);
			} else if (
					block.getType().toString().endsWith("SAND") ||
					block.getType().toString().contains("POWDER") ||
					block.getType().toString().contains("BUTTON") ||
					block.getType().toString().contains("PRESSURE_PLATE") ||
					block.getType().toString().contains("MINECART") ||
					block.getType().toString().contains("DOOR")
					) {
				event.setCancelled(true);
			}
			
			int randomNumber = r.nextInt(9);
			if (randomNumber == 5 || randomNumber == 6) {
				placer.spigot().sendMessage(new TextComponent("§cYou were warned!"));
				event.setCancelled(true);
				return;
			}

			randomNumber = r.nextInt(250);
			if (randomNumber == 21) {
				placer.kickPlayer("§6lmao -tries to build stuff-");
				return;
			}
		}
		
		// for anti-rogue-op meta; cannot place shulker boxes in creative mode
		if (block.getType().toString().contains("SHULKER_BOX")) {
			if (!PlayerMeta.isAdmin(placer)) {
				if (!placer.getGameMode().equals(GameMode.SURVIVAL)) {
					
					event.setCancelled(true);
				}				
			}
		}
		
		// anti roof-placement
		if (Config.getValue("protect.roof.noplacement").equals("true")) {
			if(block.getLocation().getY() > 127 && block.getLocation().getWorld().getName().endsWith("the_nether")) {
				event.setCancelled(true);
			}
		}
		
		// do nothing if user is placing an ender eye into an end portal frame
		if (event.getItemInHand().getType().equals(Material.ENDER_EYE)) {
			return;
			
		} else if (Config.getValue("protect.banned.place").equals("true")) {
			
			// prevent all players from placing blocks totally unobtainable in survival mode, but ignore admin account
			if (!PlayerMeta.isAdmin(placer)) {
				if (PlacementBanned.contains(block.getType())) {
					
					if (Config.getValue("protect.banned.place.ops").equals("false") && placer.isOp()) {
						return;
					}
					
					event.setCancelled(true);
					
					if (debug.equals("true") && devesp.equals("false")) {
						Bukkit.spigot().broadcast(new TextComponent(placer_name + "'s BlockPlaceEvent was cancelled."));
					}
				}
			} else {
				if (debug.equals("true") && devesp.equals("false")) {
					Bukkit.spigot().broadcast(new TextComponent(placer_name + " did this. (admin)"));
				}
			}
		}
	}
}
