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

import core.Main;
import core.frontend.ChatPrint;
import core.backend.Config;
import core.backend.utils.Util;
import core.events.ConnectionController;
import core.backend.anno.Critical;

import java.util.logging.Level;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;

@Critical
@SuppressWarnings("SpellCheckingInspection")
public class LagManager implements Listener, Runnable {
	
	// clear skulls every 1200 server-ticks (~ 60 to 120 seconds)
	@Override
	public void run() {
		
		int max_age = Integer.parseInt(Config.getValue("wither.skull.max_ticks"));				
		int removed_skulls = removeSkulls(max_age);

		Analytics.removed_skulls += removed_skulls;
		ConnectionController.joinCounter = 0;
	}
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		
		int currentWithers;
		int witherLimit = Integer.parseInt(Config.getValue("wither.limit"));
		
		if (e.getEntity() instanceof Wither) {
			
			Analytics.wither_spawns++;
			
			if (Config.debug && Config.verbose) Main.console.log(Level.INFO, "Wither Limit: " + witherLimit);
			
			currentWithers = getWithers();
			
			if (currentWithers > witherLimit-1) {
				Analytics.failed_wither_spawns++;
				e.setCancelled(true);
			}
		}

		Location spawnLoc = e.getLocation();
		String dimension = Util.getDimensionName(spawnLoc);
		EntityType thisType = e.getEntity().getType();

		if (thisType.equals(EntityType.ARMOR_STAND)) {
			int counter = 0;

			for (Entity thisEntity: e.getLocation().getChunk().getEntities()) {
				if (thisEntity.getType().equals(thisType)) counter++;
			}

			TextComponent msg = new TextComponent("17+ armor stands at " +
					spawnLoc.getX() + ", " + spawnLoc.getY() + ", " + spawnLoc.getZ() + " in " + dimension);

			String location = spawnLoc.getX() + " " + spawnLoc.getY() + " " + spawnLoc.getZ();

			msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
					"/ninjatp " + dimension + " " + location));

			if (counter > 16) Util.notifyOps(new TextComponent(ChatPrint.warn, msg));
		}
	}
	
	public static int removeSkulls(int age_limit) {

		int skulls_world; int skulls_all = 0;
		final String[] skullMsg = new String[1];

		for (org.bukkit.World thisWorld: Bukkit.getServer().getWorlds()) {
			skulls_world = 0;

			for (Entity e: thisWorld.getEntities()) {
				if (e instanceof WitherSkull) {
					if (e.getTicksLived() > age_limit) { skulls_world++; e.remove(); }
				}
			}

			if (skulls_world != 0) {
				if (skulls_world == 1) skullMsg[0] = "skull"; else skullMsg[0] = "skulls";
				if (Config.debug) Main.console.log(Level.INFO,
						"Removed " + skulls_world + " wither " + skullMsg[0] + " from " + thisWorld.getName());
			}
			skulls_all += skulls_world;
		}
		return skulls_all;
	}
	
	public static int getWithers() {
		int counter = 0;

		for (org.bukkit.World thisWorld: Bukkit.getServer().getWorlds()) {
			
			if (Config.debug && Config.verbose) Main.console.log(Level.INFO,
					"Counting withers in: " + thisWorld.getName());
			
			for (Entity e: thisWorld.getEntities()) {
				if (e instanceof Wither) {
					counter++;
				}
			}
		}
		if (Config.debug) Main.console.log(Level.INFO, "Counted Withers: " + counter);
		return counter;
	}
}
