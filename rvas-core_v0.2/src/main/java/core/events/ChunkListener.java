package core.events;

/* *
 * 
 *  About: React to chunk related events to protect vital
 *  	game-features (i.e. end exit-portal) from every kind
 *  	of exploit, and try to improve performance
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
import core.backend.Utilities;
import core.commands.Repair;
import core.tasks.Analytics;

import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ChunkLoadEvent;

import org.bukkit.World.Environment;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.Player;

public class ChunkListener implements Listener {
	
	static boolean debug = Boolean.parseBoolean(Config.getValue("debug"));
	static boolean verbose = Boolean.parseBoolean(Config.getValue("verbose"));
	
	static Material br = Material.BEDROCK;
	static Material portal = Material.END_PORTAL;
	
	public static int newCount = 0;
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLoad(ChunkLoadEvent event) {
		
		Analytics.loaded_chunks++;
		Chunk chunk = event.getChunk();
		
		if (!event.isNewChunk()) {

			int y_low = Utilities.getExitFloor(chunk);
			if (y_low == -1) y_low = Repair.y_default;

			fixEndExit(chunk, y_low);
			chunk.setForceLoaded(false); // WARNING: this line will interfere with force-loaded spawn chunks
			
			if (Config.getValue("chunk.load.repair_roof").equals("true")) repairBedrockROOF(chunk, null);
			if (Config.getValue("chunk.load.repair_floor").equals("true")) repairBedrockFLOOR(chunk, null);
			
			try {
				antiFurnaceBan(chunk);
			} catch (Exception e) {
				System.out.println(e);
			}
			
		} else {ChunkListener.newCount++; Analytics.new_chunks++;}
	}
	
	public static void antiFurnaceBan(Chunk chunk) {
		
		int furnace_count = Utilities.blockCounter(chunk, Material.FURNACE);
		
		// limit furnace count to 2 sub-chunks worth of furnaces per chunk
		if (furnace_count > 8192) {
			
			System.out.println("WARNING: TOO MANY FURNACES. Removing 90% of them..");
			Utilities.blockRemover(chunk, Material.FURNACE, (int)Math.rint((double)furnace_count * 0.9));
		}
	}

	public static int y_low = 62;
	public static int y_high = 63;
	public static boolean foundPortal = false;

	public static void fixEndExit(Chunk chunk, int y) { // <- intentionally ignores central pillar
		
		DragonBattle dragon = chunk.getWorld().getEnderDragonBattle();
		
		if (chunk.getWorld().getEnvironment().equals(Environment.THE_END)) {
			if (dragon == null || !dragon.hasBeenPreviouslyKilled()) {

				Utilities.notifyOps(new TextComponent("Cannot repair portal; dragon has never been killed!"));
				return;
			}

			int x_chunk = chunk.getX();
			int z_chunk = chunk.getZ();

			// NW Quadrant
			if (x_chunk == -1 && z_chunk == -1) {
				System.out.println("NW EXIT PORTAL CHUNK LOADED");

				chunk.setForceLoaded(false);

				chunk.getBlock(15, y_low, 15).setType(br);
				chunk.getBlock(15, y_low, 14).setType(br);
				chunk.getBlock(14, y_low, 15).setType(br);

				chunk.getBlock(15, y_high, 15).setType(portal);
				chunk.getBlock(15, y_high, 14).setType(portal);
				chunk.getBlock(15, y_high, 13).setType(br);

				chunk.getBlock(14, y_high, 15).setType(portal);
				chunk.getBlock(14, y_high, 14).setType(br);
				chunk.getBlock(13, y_high, 15).setType(br);
			}

			// SW Quadrant
			if (x_chunk == -1 && z_chunk == 0) {
				System.out.println("SW EXIT PORTAL CHUNK LOADED");

				chunk.setForceLoaded(false);

				chunk.getBlock(15, y_low, 0).setType(br);
				chunk.getBlock(14, y_low, 0).setType(br);
				chunk.getBlock(15, y_low, 1).setType(br);
				chunk.getBlock(14, y_low, 1).setType(br);
				chunk.getBlock(15, y_low, 2).setType(br);

				chunk.getBlock(15, y_high, 0).setType(portal);
				chunk.getBlock(14, y_high, 0).setType(portal);
				chunk.getBlock(13, y_high, 0).setType(br);

				chunk.getBlock(15, y_high, 1).setType(portal);
				chunk.getBlock(14, y_high, 1).setType(portal);
				chunk.getBlock(13, y_high, 1).setType(br);

				chunk.getBlock(15, y_high, 2).setType(portal);
				chunk.getBlock(14, y_high, 2).setType(br);
				chunk.getBlock(15, y_high, 3).setType(br);
			}

			// NE Quadrant
			if (x_chunk == 0 && z_chunk == -1) {
				System.out.println("NE EXIT PORTAL CHUNK LOADED");

				chunk.setForceLoaded(false);

				chunk.getBlock(0, y_low, 15).setType(br);
				chunk.getBlock(1, y_low, 15).setType(br);
				chunk.getBlock(2, y_low, 15).setType(br);
				chunk.getBlock(0, y_low, 14).setType(br);
				chunk.getBlock(1, y_low, 14).setType(br);

				chunk.getBlock(0, y_high, 15).setType(portal);
				chunk.getBlock(1, y_high, 15).setType(portal);
				chunk.getBlock(2, y_high, 15).setType(portal);
				chunk.getBlock(3, y_high, 15).setType(br);

				chunk.getBlock(0, y_high, 14).setType(portal);
				chunk.getBlock(1, y_high, 14).setType(portal);
				chunk.getBlock(2, y_high, 14).setType(br);

				chunk.getBlock(0, y_high, 13).setType(br);
				chunk.getBlock(1, y_high, 13).setType(br);
			}

			// SE Quadrant
			if (x_chunk == 0 && z_chunk == 0) {
				System.out.println("SE EXIT PORTAL CHUNK LOADED");

				chunk.setForceLoaded(false);

				chunk.getBlock(0, y_low, 0).setType(br);
				chunk.getBlock(0, y_low, 1).setType(br);
				chunk.getBlock(0, y_low, 2).setType(br);

				chunk.getBlock(1, y_low, 0).setType(br);
				chunk.getBlock(1, y_low, 1).setType(br);
				chunk.getBlock(1, y_low, 2).setType(br);

				chunk.getBlock(2, y_low, 0).setType(br);
				chunk.getBlock(2, y_low, 1).setType(br);

				chunk.getBlock(0, y_high, 0).setType(br);
				chunk.getBlock(0, y_high, 1).setType(portal);
				chunk.getBlock(0, y_high, 2).setType(portal);
				chunk.getBlock(0, y_high, 3).setType(br);

				chunk.getBlock(1, y_high, 0).setType(portal);
				chunk.getBlock(1, y_high, 1).setType(portal);
				chunk.getBlock(1, y_high, 2).setType(portal);
				chunk.getBlock(1, y_high, 3).setType(br);

				chunk.getBlock(2, y_high, 0).setType(portal);
				chunk.getBlock(2, y_high, 1).setType(portal);
				chunk.getBlock(2, y_high, 2).setType(br);

				chunk.getBlock(3, y_high, 0).setType(br);
				chunk.getBlock(3, y_high, 1).setType(br);
			}
		}
	}
	
	public static void repairBedrockROOF(Chunk chunk, Player receiver) {
		
		if (chunk.getWorld().getEnvironment().equals(Environment.NETHER)) {
			
			int counter = 0;
			int i_x = 0;
			int i_z;
			
			while (i_x <= 15 ) {

				i_z = 0;				
				while (i_z <= 15) {
					
					if (chunk.getBlock(i_x, 127, i_z).getType() != br) counter++;
					chunk.getBlock(i_x, 127, i_z).setType(br);
										
					i_z++;
				}
				i_x++;
			}
			
			if (debug && counter != 0) {
				System.out.println(counter + " bedrock blocks replaced:");
				System.out.println("Dimension: " + chunk.getWorld().getEnvironment()
						+ " | Chunk section coords: " + chunk.getX() + ", " + chunk.getZ());
				System.out.println();
				
				if (receiver != null) {
					receiver.spigot().sendMessage(new TextComponent(counter + " bedrock blocks replaced!"));
				}
			}
		}
	}
	
	public static void repairBedrockFLOOR(Chunk chunk, Player receiver) {
		
		if (!chunk.getWorld().getEnvironment().equals(Environment.THE_END)) {
			
			int counter = 0;
			int i_x = 0;
			int i_z;
			
			while (i_x <= 15 ) {

				i_z = 0;				
				while (i_z <= 15) {
					
					if (chunk.getBlock(i_x, 0, i_z).getType() != br) counter++;
					chunk.getBlock(i_x, 0, i_z).setType(br);
										
					i_z++;
				}
				i_x++;
			}
			
			if (debug && counter != 0) {
				System.out.println(counter + " bedrock blocks replaced:");
				System.out.println("Dimension: " + chunk.getWorld().getEnvironment()
						+ " | Chunk section coords: " + chunk.getX() + ", " + chunk.getZ());
				System.out.println();
				
				if (receiver != null) {
					receiver.spigot().sendMessage(new TextComponent(counter + " bedrock blocks replaced!"));
				}
			}
		}
	}
}
