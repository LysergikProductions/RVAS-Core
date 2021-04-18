package core.events;

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

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.World.Environment;

import core.backend.Config;

public class LagPrevention implements Listener, Runnable {
	public static int currentWithers = 0;

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {

		if (e.getEntity() instanceof Wither) {
			
			int witherLimit = Integer.parseInt(Config.getValue("wither.limit"));
			currentWithers = getWithers();
			
			if (currentWithers + 1 > witherLimit) {
				e.setCancelled(true);
				return;
			}
		}
	}

	public static int getWithers() { // disabled for performance reasons; to reimplement later
		
		int counter = 0;		
		int witherLimit = Integer.parseInt(Config.getValue("wither.limit"));
		
		for (World thisWorld: Bukkit.getServer().getWorlds()) {
			System.out.println("Counting withers in: " + thisWorld.getName());
			
			for (Entity e: thisWorld.getEntities()) {
				if (e instanceof Wither) {
					counter++;
				}
			}
		}
		return counter;
	}
	
	// clear skulls every 1200 server-ticks (~ 60 to 120 seconds)
	@Override
	public void run() {
		
		for (Player onlinePlayer: Bukkit.getServer().getOnlinePlayers()) {
			if (onlinePlayer.isOp()) {
				
				onlinePlayer.chat("/kill @e[type=minecraft:wither_skull]");
				return;
			}
		}
		
		System.out.println("No ops online. Using bukkit to clear skulls..");
		
		for (World thisWorld: Bukkit.getServer().getWorlds()) {
			System.out.println("Clearing wither skulls in: " + thisWorld.getName());
			
			for (Entity e: thisWorld.getEntities()) {
				if (e instanceof WitherSkull) {
					
					if (e.getTicksLived() > 600) e.remove();
				}
			}
		}
	}
}
