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
import core.backend.PlayerMeta;

import java.util.*;
import java.text.DecimalFormat;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ChunkLoadEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.boss.DragonBattle;

public class ChunkListener implements Listener {
	
	static boolean debug = Boolean.parseBoolean(Config.getValue("debug"));
	static boolean devesp = Boolean.parseBoolean(Config.getValue("devesp"));
	
	static Material br = Material.BEDROCK;
	static Material portal = Material.END_PORTAL;
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onLoad(ChunkLoadEvent event) {
		
		Chunk chunk = event.getChunk();
		
		if (!event.isNewChunk()) {
			
			fixEndExit(chunk);
			
			if (Config.getValue("chunk.load.repair_roof").equals("true")) repairBedrockROOF(chunk);
			if (Config.getValue("chunk.load.repair_floor").equals("true")) repairBedrockFLOOR(chunk);
			
		} else if (debug && !devesp) System.out.println("Generating brand new chunk..");
	}
	
	public static void fixEndExit(Chunk chunk) {
		
		DragonBattle dragon = chunk.getWorld().getEnderDragonBattle();
		
		if (chunk.getWorld().getEnvironment().equals(Environment.THE_END)
				&& dragon.hasBeenPreviouslyKilled()) {
		
			int x_chunk = chunk.getX();
			int z_chunk = chunk.getZ();
			
			// NW Quadrant
			if (x_chunk == -1 && z_chunk == -1) {
				System.out.println("NW EXIT PORTAL CHUNK LOADED");
				
				/*chunk.setForceLoaded(false);
				
				chunk.getBlock(-1, 63, -1).setType(br);
				chunk.getBlock(-1, 63, -2).setType(br);
				chunk.getBlock(-2, 63, -1).setType(br);
				
				chunk.getBlock(-1, 64, -1).setType(portal);
				chunk.getBlock(-1, 64, -2).setType(portal);
				chunk.getBlock(-1, 64, -3).setType(br);
				
				chunk.getBlock(-2, 64, -1).setType(portal);
				chunk.getBlock(-2, 64, -2).setType(br);
				chunk.getBlock(-3, 64, -1).setType(br);*/
			}
			
			// SW Quadrant
			if (x_chunk == -1 && z_chunk == 0) {
				System.out.println("SW EXIT PORTAL CHUNK LOADED");
				
				/*chunk.setForceLoaded(false);
				
				chunk.getBlock(-1, 63, 0).setType(br);
				chunk.getBlock(-2, 63, 0).setType(br);
				chunk.getBlock(-1, 63, 1).setType(br);
				chunk.getBlock(-2, 63, 1).setType(br);
				chunk.getBlock(-1, 63, 2).setType(br);
				
				chunk.getBlock(-1, 64, 0).setType(portal);
				chunk.getBlock(-2, 64, 0).setType(portal);
				chunk.getBlock(-3, 64, 0).setType(br);
				
				chunk.getBlock(-1, 64, 1).setType(portal);
				chunk.getBlock(-2, 64, 1).setType(portal);
				chunk.getBlock(-3, 64, 0).setType(br);
				
				chunk.getBlock(-1, 64, 2).setType(portal);
				chunk.getBlock(-2, 64, 2).setType(br);
				chunk.getBlock(-1, 64, 3).setType(br);*/
			}
			
			// NE Quadrant
			if (x_chunk == 0 && z_chunk == -1) {
				System.out.println("NE EXIT PORTAL CHUNK LOADED");
				/*
				chunk.setForceLoaded(false);
				
				chunk.getBlock(0, 63, -1).setType(br);
				chunk.getBlock(1, 63, -1).setType(br);
				chunk.getBlock(2, 63, -1).setType(br);
				chunk.getBlock(0, 63, -2).setType(br);
				chunk.getBlock(1, 63, -2).setType(br);
				
				chunk.getBlock(0, 64, -1).setType(portal);
				chunk.getBlock(1, 64, -1).setType(portal);
				chunk.getBlock(2, 64, -1).setType(portal);
				chunk.getBlock(2, 64, -1).setType(br);
				
				chunk.getBlock(0, 64, -2).setType(portal);
				chunk.getBlock(1, 64, -2).setType(portal);
				chunk.getBlock(2, 64, -2).setType(br);
				
				chunk.getBlock(0, 64, -3).setType(br);
				chunk.getBlock(1, 64, -3).setType(br);*/
			}
			
			// SE Quadrant
			if (x_chunk == 0 && z_chunk == 0) {
				System.out.println("SE EXIT PORTAL CHUNK LOADED");
				
				/*chunk.setForceLoaded(false);
				
				chunk.getBlock(0, 63, 0).setType(br);
				chunk.getBlock(0, 63, 1).setType(br);
				chunk.getBlock(0, 63, 2).setType(br);
				
				chunk.getBlock(1, 63, 0).setType(br);
				chunk.getBlock(1, 63, 1).setType(br);
				chunk.getBlock(1, 63, 2).setType(br);
				
				chunk.getBlock(2, 63, 0).setType(br);
				chunk.getBlock(2, 63, 1).setType(br);
				chunk.getBlock(2, 63, 2).setType(br);
				
				chunk.getBlock(0, 64, 0).setType(br);
				chunk.getBlock(0, 64, 1).setType(portal);
				chunk.getBlock(0, 64, 2).setType(portal);
				chunk.getBlock(0, 64, 3).setType(br);
				
				chunk.getBlock(1, 64, 0).setType(portal);
				chunk.getBlock(1, 64, 1).setType(portal);
				chunk.getBlock(1, 64, 2).setType(portal);
				chunk.getBlock(1, 64, 3).setType(br);
				
				chunk.getBlock(2, 64, 0).setType(portal);
				chunk.getBlock(2, 64, 1).setType(portal);
				chunk.getBlock(2, 64, 2).setType(br);
				
				chunk.getBlock(3, 64, 0).setType(br);
				chunk.getBlock(3, 64, 1).setType(br);*/
			}
		}
	}
	
	public static void repairBedrockROOF(Chunk chunk) {
		
		int i_x = 0;
		int i_z = 0;
		
		while (i_x > 17 ) { i_x++;
			while (i_z > 17) { i_z++;
				int x_corner = chunk.getX()*16;
				int z_corner = chunk.getZ()*16;
				
				Block thisBlock = chunk.getBlock(i_x + x_corner, 127, i_z + z_corner);
				thisBlock.setType(br);
			}
		}
	}
	
	public static void repairBedrockFLOOR(Chunk chunk) {
		// set all blocks at y==0 in overworld and the_nether to bedrock
	}
}
