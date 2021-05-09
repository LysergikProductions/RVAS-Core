package core.tasks;

/* *
 * 
 *  About: Manages potentially lag-inducing situations in various ways
 *  	by clearing entities and checking entity counts
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

import core.events.Connection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class LagManager implements Listener, Runnable {
	
	static boolean debug = Boolean.parseBoolean(Config.getValue("debug"));
	static boolean verbose = Boolean.parseBoolean(Config.getValue("verbose"));
	
	// clear skulls every 1200 server-ticks (~ 60 to 120 seconds)
	@Override
	public void run() {
		
		int max_age = Integer.parseInt(Config.getValue("wither.skull.max_ticks"));				
		int removed_skulls = removeSkulls(max_age);

		Analytics.removed_skulls += removed_skulls;
		Connection.joinCounter = 0;
	}
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		
		int currentWithers;
		int witherLimit = Integer.parseInt(Config.getValue("wither.limit"));
		
		if (e.getEntity() instanceof Wither) {
			
			Analytics.wither_spawns++;
			
			if (debug && verbose) System.out.println("Wither Limit: " + witherLimit);
			
			currentWithers = getWithers();
			
			if (currentWithers > witherLimit-1) {
				Analytics.failed_wither_spawns++;
				e.setCancelled(true);
			}
		}

		Location spawnLoc = e.getLocation();
		String dimension = Utilities.getDimensionName(spawnLoc);
		EntityType thisType = e.getEntity().getType();

		if (thisType.equals(EntityType.ARMOR_STAND)) {
			int counter = 0;

			for (Entity thisEntity: e.getLocation().getChunk().getEntities()) {
				if (thisEntity.getType().equals(thisType)) counter++;
			}

			TextComponent warn = new TextComponent("WARN "); warn.setBold(true);
			warn.setColor(ChatColor.RED);

			TextComponent msg = new TextComponent("17+ armor stands at " +
					spawnLoc.getX() + ", " + spawnLoc.getY() + ", " + spawnLoc.getZ() + " in " + dimension);

			String cmd = "/execute in " + dimension + " run tp @s " +
					spawnLoc.getX() + " " + spawnLoc.getY() + " " + spawnLoc.getZ();

			msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));

			if (counter > 16) Utilities.notifyOps(new TextComponent(warn, msg));
		}
	}
	
	public static int removeSkulls(int age_limit) {
		
		int skulls_world;
		int skulls_all = 0;
		
		String skullMsg;
		
		for (World thisWorld: Bukkit.getServer().getWorlds()) {
			
			skulls_world = 0;
			
			for (Entity e: thisWorld.getEntities()) {
				if (e instanceof WitherSkull) {
					if (e.getTicksLived() > age_limit) skulls_world++; e.remove();
				}
			}
			
			if (skulls_world != 0) {
				
				if (skulls_world == 1) skullMsg = "skull"; else skullMsg = "skulls";
				if (debug) System.out.println(
						"Removed " + skulls_world + " wither " + skullMsg + " from " + thisWorld.getName());
			}
			skulls_all += skulls_world;
		}
		return skulls_all;
	}
	
	public static int getWithers() {
		int counter = 0;

		for (World thisWorld: Bukkit.getServer().getWorlds()) {
			
			if (debug && verbose) System.out.println("Counting withers in: " + thisWorld.getName());
			
			for (Entity e: thisWorld.getEntities()) {
				if (e instanceof Wither) {
					counter++;
				}
			}
		}
		if (debug) System.out.println("Counted Withers: " + counter);
		return counter;
	}

	public static boolean updateConfigs() {

		try {
			debug = Boolean.parseBoolean(Config.getValue("debug"));
			verbose = Boolean.parseBoolean(Config.getValue("verbose"));

			return true;

		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
}