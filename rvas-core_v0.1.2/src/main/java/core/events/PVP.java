package core.events;

/* *
 * 
 *  About: Listen for PVP related events to do various things,
 *  	primarily incrementing PVP related stats hash maps
 *  	stored in core.backend.PlayerMeta
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

import io.papermc.paper.event.entity.EntityMoveEvent;
import net.md_5.bungee.api.chat.TextComponent;
import java.util.UUID;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.Bukkit;

import core.backend.Config;
import core.backend.PlayerMeta;
import core.backend.Utilities;

public class PVP implements Listener {

	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		
		if (Config.getValue("debug").equals("true") && Config.getValue("devesp").equals("false")) {
			System.out.println("[core.events.pvp] onKill has been called");
		}
		
		Player killed = event.getEntity();
		UUID killedID = killed.getUniqueId();
		Player killer = killed.getKiller();
		
		try {
			UUID killerID = killer.getUniqueId();
		} catch (NullPointerException e) {
			System.out.println("killerID in PVP.java is null.");
			return;
		}
		
		String killedName = killed.getName();
		String killerName = "";
		String killerLoc = "";
		
		if (killer != null && Config.getValue("debug").equals("true")) {
			
			killerName = killer.getName();
			killerLoc = killer.getLocation().getX()+", "+killer.getLocation().getY()+", "+killer.getLocation().getZ();
			
			System.out.println("[core.events.pvp] "+killerName+" "+killedName+" "+killerLoc);
			
		} else if (Config.getValue("debug").equals("true")){
			
			System.out.println("[core.events.pvp] killer = null");
		}
		
		// increment appropriate stats
		PlayerMeta.incKillTotal(killer, 1);
		PlayerMeta.incDeathTotal(killed, 1);
		
		// check if victim was in the spawn region on death
		OfflinePlayer victim = Bukkit.getOfflinePlayer(killedID);
		double victim_playtime = PlayerMeta.getPlaytime(victim);
		
		Double cX = killed.getLocation().getX();
		Double cZ = killed.getLocation().getZ();
		
		double max_x; double max_z;
		double min_x; double min_z;
		
		Double config_max_x = Double.parseDouble(Config.getValue("spawn.max.X"));
		Double config_max_z = Double.parseDouble(Config.getValue("spawn.max.Z"));
		Double config_min_x = Double.parseDouble(Config.getValue("spawn.min.X"));
		Double config_min_z = Double.parseDouble(Config.getValue("spawn.min.Z"));
		
		if (config_max_x.isNaN()) max_x = 420.0; else max_x = config_max_x.doubleValue();
		if (config_max_z.isNaN()) max_z = 420.0; else max_z = config_max_z.doubleValue();	
		if (config_min_x.isNaN()) min_x = -420.0; else min_x = config_min_x.doubleValue();
		if (config_min_z.isNaN()) min_z = -420.0; else min_z = config_min_z.doubleValue();
		
		if (cX == null || cZ == null) {
			
			System.out.println("[core.events.PVP] failed to retrieve location for victim: " + killedName);
			return;
			
		} else if (cX < max_x && cZ < max_z && cX > min_x && cZ > min_z) {
			
			System.out.println(killedName + " was killed in the spawn region!");
			// check if victim is a new player
			if (victim_playtime < 3600.0 && killer != null) {
				
				System.out.println(killedName + " was also a new player!");
				PlayerMeta.incSpawnKill(killer, 1);
			}
		}
	}
}
