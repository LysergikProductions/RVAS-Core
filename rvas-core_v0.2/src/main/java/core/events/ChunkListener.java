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

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ChunkLoadEvent;

import org.bukkit.World.Environment;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.Player;

@SuppressWarnings({"SpellCheckingInspection", "deprecation"})
public class ChunkListener implements Listener {
	
	static Material br = Material.BEDROCK;
	static Material portal = Material.END_PORTAL;

	public static int newCount = 0;
	public static boolean foundExitPortal = false;
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLoad(ChunkLoadEvent event) {
		Analytics.loaded_chunks++;

		Chunk chunk = event.getChunk();
		Environment dimension = chunk.getWorld().getEnvironment();

		int x = chunk.getX(); int z = chunk.getZ();

		if (!event.isNewChunk()) {
			chunk.setForceLoaded(false); // WARNING: this line will interfere with force-loaded spawn chunks

			try {
				if (!foundExitPortal && dimension.equals(Environment.THE_END)) {
					if (x == 0 && z == 0) Repair.y_low = Utilities.getExitFloor(chunk);
					if (Repair.y_low != -1) ChunkListener.foundExitPortal = true;
					else Repair.y_low = Repair.y_default;
				}

				if (dimension.equals(Environment.THE_END)) {
					if (x == 0 && z == 0) fixEndExit(chunk, Repair.y_low);
					if (x == 0 && z == -1) fixEndExit(chunk, Repair.y_low);
					if (x == -1 && z == 0) fixEndExit(chunk, Repair.y_low);
					if (x == -1 && z == -1) fixEndExit(chunk, Repair.y_low);
				}
			} catch (Exception e) {
				System.out.println("Failed to check/repair end-exit..");
				if (Config.debug) System.out.println(e.getMessage());
				if (Config.verbose) e.printStackTrace();
			}
			
			if (Config.getValue("chunk.load.repair_roof").equals("true")) repairBedrockROOF(chunk, null);
			if (Config.getValue("chunk.load.repair_floor").equals("true")) repairBedrockFLOOR(chunk, null);

			antiChunkBan(chunk);
			
		} else {ChunkListener.newCount++; Analytics.new_chunks++;}
	}
	
	public static void antiChunkBan(Chunk chunk) {
		int removed_blocks = 0; int total_count;

		try {
			total_count = Utilities.banBlockCounter(chunk);
		} catch (Exception e) {
			e.printStackTrace();
			total_count = 0;
		}
		
		// limit count to 2 sub-chunks worth of ban blocks per chunk
		if (total_count > 8192) {
			
			System.out.println("WARN: TOO MANY BAN BLOCKS. Popping 90% of them..");
			removed_blocks = Utilities.banBlockRemover(chunk, (int)Math.rint((double)total_count * 0.9));

			if (Config.debug) System.out.println("Removed " + removed_blocks + " chunk-banning blocks");
		}
	}

	public static void fixEndExit(Chunk chunk, int y) { // <- intentionally ignores central pillar
		DragonBattle dragon = chunk.getWorld().getEnderDragonBattle();

		int y_high = y+1;
		
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

				chunk.getBlock(15, y, 15).setType(br);
				chunk.getBlock(15, y, 14).setType(br);
				chunk.getBlock(14, y, 15).setType(br);

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

				chunk.getBlock(15, y, 0).setType(br);
				chunk.getBlock(14, y, 0).setType(br);
				chunk.getBlock(15, y, 1).setType(br);
				chunk.getBlock(14, y, 1).setType(br);
				chunk.getBlock(15, y, 2).setType(br);

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

				chunk.getBlock(0, y, 15).setType(br);
				chunk.getBlock(1, y, 15).setType(br);
				chunk.getBlock(2, y, 15).setType(br);
				chunk.getBlock(0, y, 14).setType(br);
				chunk.getBlock(1, y, 14).setType(br);

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

				chunk.getBlock(0, y, 0).setType(br);
				chunk.getBlock(0, y, 1).setType(br);
				chunk.getBlock(0, y, 2).setType(br);

				chunk.getBlock(1, y, 0).setType(br);
				chunk.getBlock(1, y, 1).setType(br);
				chunk.getBlock(1, y, 2).setType(br);

				chunk.getBlock(2, y, 0).setType(br);
				chunk.getBlock(2, y, 1).setType(br);

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
			
			if (Config.debug && counter != 0) {
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
			
			if (Config.debug && counter != 0) {
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
