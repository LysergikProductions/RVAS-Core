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
		
		/*// check if victim was in the spawn region on death
		Double victim_playtime = Double.parseDouble(Utilities.calculateTime(PlayerMeta.getPlaytime(killed)));
		
		Double cX = killed.getLocation().getX();
		Double cZ = killed.getLocation().getZ();
		
		if (cX == null || cZ == null) {
			
			System.out.println("[core.events.PVP] failed to retrieve location for victim: " + killedName);
			return;
			
		} else if (cX < 710 && cZ < 710 && cX > -710 && cZ > -710) { // spawn region will eventually be config defined
			
			System.out.println(killedName + " was killed in the spawn region!");
			// check if victim is a new player
			if (victim_playtime < 3600 && killer != null) {
				
				System.out.println(killedName + " was also a new player!");
				PlayerMeta.incSpawnKill(killer, 1);
			}
		}*/
	}
}
