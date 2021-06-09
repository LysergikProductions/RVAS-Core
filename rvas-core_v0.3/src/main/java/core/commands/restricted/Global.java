package core.commands.restricted;

/* *
 * 
 *  About: Global effects for use on special occasions or during events
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

import core.backend.ChatPrint;
import core.data.PlayerMeta;
import core.backend.utils.Util;
import core.events.SpawnController;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Global implements CommandExecutor {

	public static TextComponent dreamMsg; static {
		dreamMsg = new TextComponent("You wake up, confused.. was that a dream?");
		dreamMsg.setColor(ChatPrint.faded);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		Player op = (Player)sender;

		if (args.length != 0) {
			switch (args[0].toUpperCase()) {

				case "ZAP":

					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						Location player_loc = p.getLocation();

						int playerX = player_loc.getBlockX();
						int playerZ = player_loc.getBlockZ();

						// prevent setting fire to bases by checking distance to either axis
						if (Math.abs(playerX) > 1024 || Math.abs(playerZ) > 1024) {

							for (int i = 0; i < 3; i++) {
								player_loc.setX(player_loc.getX() + Util.getRandomNumber(playerX-8, playerX+8));
								player_loc.setZ(player_loc.getZ() + Util.getRandomNumber(playerZ-8, playerZ+8));

								p.getWorld().spigot().strikeLightning(player_loc, false);
								player_loc.setX(playerX); player_loc.setZ(playerZ);
							}
						}
					} return true;

				case "DREAM":

					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						if (p.isOp() || PlayerMeta.isAdmin(p)) continue;

						Location playerSpawn = p.getBedSpawnLocation();
						Location baseLoc = p.getLocation();
						Location finalTP = null; Vector distance;

						if (playerSpawn != null) {

							distance = playerSpawn.subtract(baseLoc).toVector();
							if (distance.length() > 1024) continue;

							finalTP = playerSpawn;

						} else finalTP = SpawnController.getRandomEllipseSpawn(baseLoc.getWorld(), baseLoc);

						String player_name = p.getName();

						String x = String.valueOf(finalTP.getBlockX());
						String y = String.valueOf(finalTP.getBlockY());
						String z = String.valueOf(finalTP.getBlockZ());

						op.chat("/tp " + player_name + " " + x + " " + y + " " + z);
						p.sendMessage(dreamMsg.toLegacyText());
					}
			}
		} return true;
	}
}
