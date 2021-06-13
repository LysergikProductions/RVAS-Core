package core.events;

/* *
 * 
 *  About: Listen for PVP related events to do various things,
 *  	primarily incrementing PVP related stats hash maps
 *  	stored in core.data.PlayerMeta
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
import core.backend.Config;
import core.backend.utils.Util;
import core.data.PlayerMeta;
import core.data.StatsManager;

import java.util.UUID;
import java.util.Objects;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PVP implements Listener {
	
	@EventHandler
	public void onKill(PlayerDeathEvent event) {

		Player killed = event.getEntity();
		Player killer = killed.getKiller();
		UUID killedID = killed.getUniqueId();

		String killerName, killerLoc;
		String killedName = killed.getName();
		
		if (Config.debug) {
			try {
				killerName = Objects.requireNonNull(killer).getName();
				killerLoc = killer.getLocation().getX() +
						", "+killer.getLocation().getY() +
						", "+killer.getLocation().getZ();

			} catch (Exception ignore) {
				if (Config.debug) Main.console.log(Level.INFO, "Killer was null!");

				killerName = "null";
				killerLoc = killed.getLocation().getX() +
						", "+killed.getLocation().getY() +
						", "+killed.getLocation().getZ();
			}
			if (Config.debug) Main.console.log(Level.INFO,
					"[core.events.pvp] "+killerName+" "+killedName+" "+killerLoc);
		}

		// increment appropriate stats, do nothing if this was not a PVP kill
		if (killer != null) {
			StatsManager.incKillTotal(killer, 1);
			StatsManager.incDeathTotal(killed, 1);
		} else return;

		// check if victim was in the spawn region on death
		OfflinePlayer victim = Bukkit.getOfflinePlayer(killedID);
		double victim_playtime = PlayerMeta.getPlaytime(victim);

		if (Util.isInSpawn(killed.getLocation())) {
			if (Config.debug && Config.verbose) Main.console.log(
					Level.INFO, killedName + " was killed in the spawn region!");

			if (victim_playtime < 3600.0) {
				if (Config.debug && Config.verbose) Main.console.log(Level.INFO, killedName + " was also a new player!");
				StatsManager.incSpawnKill(killer, 1);
			}
		}
	}
}
