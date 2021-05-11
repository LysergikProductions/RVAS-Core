package core.commands;

/* *
 * 
 *  About: Global effects for use on special occasions
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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Global implements CommandExecutor {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		
		// check args
		if (args.length != 0) {
			
			switch (args[0].toUpperCase()) {
				case "ZAP":		
					int i = 0;
					
					while (i < 3) { i++;
						
						for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							
							Location player_loc = p.getLocation();
							
							player_loc.setX(player_loc.getX()+7.10+i);
							player_loc.setZ(player_loc.getZ()+7.10-i);
							
							p.getWorld().spigot().strikeLightning(player_loc, false);
						}
					}	
					return true;
			}
		}
		return true;
	}
}
