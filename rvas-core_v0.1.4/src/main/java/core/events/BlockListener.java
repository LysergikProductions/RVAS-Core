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
import java.text.DecimalFormat;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
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
	
	// get configs
	static String roofProt = Config.getValue("protect.bedrock.roof");
	static String floorProt = Config.getValue("protect.bedrock.floor");
	static boolean debug = Boolean.parseBoolean(Config.getValue("debug"));
	static boolean devesp = Boolean.parseBoolean(Config.getValue("devesp"));
	
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
		
		//long startTime = System.nanoTime();
		
		// get commonly referenced data
		Player breaker = event.getPlayer();
		GameMode mode = breaker.getGameMode();		
		String breaker_name = breaker.getName();
		
		Block block = event.getBlock();
		Material blockType = block.getType();
		Location block_loc = block.getLocation();
		
		// prevent creative players from breaking certain blocks but completely ignore admin account
		if (!PlayerMeta.isAdmin(breaker)) {
			
			if (!breaker.isOp()) breaker.setGameMode(GameMode.SURVIVAL);
			
			if (BreakBanned.contains(blockType)) {
				
				event.setCancelled(true);
				
				if (debug && !devesp) {
					Bukkit.spigot().broadcast(new TextComponent(breaker_name + "'s BlockBreakEvent was cancelled."));
				}
				
			// do things if block == bedrock
			} else if (blockType.equals(Material.BEDROCK)) {
				
				Environment dimension = block.getWorld().getEnvironment();
				
				if (debug && !devesp) Bukkit.spigot().broadcast(
						new TextComponent(breaker_name + " just broke a bedrock block!"));
				
				// protect bedrock floor
				if (block_loc.getY() < 1 && floorProt.equals("true")) {
					
					event.setCancelled(true);
					
					if (debug && !devesp) Bukkit.spigot().broadcast(
							new TextComponent(breaker_name + "'s BlockBreakEvent was cancelled."));
					
				// protect nether roof	
				} else if (dimension.equals(Environment.NETHER) && block_loc.getY() == 127 && roofProt.equals("true")) {
					
					event.setCancelled(true);

					if (debug && !devesp) {
						Bukkit.spigot().broadcast(new TextComponent(breaker_name + "'s BlockBreakEvent was cancelled."));
					}
				} else if (dimension.equals(Environment.THE_END) && block_loc.getY() == 64) {
					
					if (block_loc.getX() == 64) {
						
					}
				}
			}
		}
		//long endTime = System.nanoTime();
		//long duration = (endTime - startTime);
		//System.out.println("BreakTime: " + new DecimalFormat("#.###").format((double)duration/1000000.0) + " ms");
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		
		//long startTime = System.nanoTime();		
		//System.out.println("onPlace triggered..");
		
		Player placer = event.getPlayer();
		
		// ignore event if placer is the owner
		boolean admin_clearance = false;
		if (PlayerMeta.isAdmin(placer)) return;
		
		Block block = event.getBlockPlaced();
		Location block_loc = block.getLocation();
		GameMode mode = placer.getGameMode();		
		String placer_name = placer.getName();
		
		Material blockType = block.getType();
		String mat = blockType.toString();
		
		// Make game unplayable for laggers
		// this method currently always returns false
		// in this build, so this block doesn't run
		if (PlayerMeta.isLagfag(placer)) {
			
			if (LagMats.contains(blockType)) {
				event.setCancelled(true);
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
		if (mat.contains("SHULKER_BOX")) {
			if (!placer.getGameMode().equals(GameMode.SURVIVAL)) {
					
				event.setCancelled(true);
				return;		
			}
		}
		
		// anti roof-placement
		if (Config.getValue("protect.roof.noplacement").equals("true")) {
			if(block_loc.getY() > 127 && block_loc.getWorld().getName().endsWith("the_nether")) {
				
				event.setCancelled(true);
				return;
			}
		}
		
		// do nothing if user is placing an ender eye into an end portal frame
		if (event.getItemInHand().getType().equals(Material.ENDER_EYE)) {
			return;
			
		} else if (Config.getValue("protect.banned.place").equals("true")) {
			
			// prevent all players from placing blocks totally unobtainable in survival mode, but ignore admin account
					
			if (Config.getValue("protect.banned.place.ops").equals("false") && placer.isOp()) {
				return;
			}
			
			if (PlacementBanned.contains(blockType)) {
				event.setCancelled(true);
				
				if (debug && !devesp) Bukkit.spigot().broadcast(
						new TextComponent(placer_name + "'s BlockPlaceEvent was cancelled."));
			}
			
			return;
		}
		
		//long endTime = System.nanoTime();
		//long duration = (endTime - startTime);
		//System.out.println("PlaceTime: " + new DecimalFormat("#.###").format((double)duration/1000000.0) + " ms");
	}
	
	// try to prevent ghost blocks
	// this should only trigger if the player place event wasn't cancelled
	@EventHandler(priority = EventPriority.LOW)
	public void noGhost(BlockPlaceEvent event) {
		
		System.out.println("Checking for ghost placement..");
		
		Block blockToPlace = event.getBlockPlaced();
		Material blockType = blockToPlace.getType();
		Location block_loc = blockToPlace.getLocation();
		
		Block blockInGame = block_loc.getBlock();
		blockInGame.setType(blockType);
		
		System.out.println("blockToPlace: " + blockType + " blockToSet: " + blockInGame.getType());
	}
}
