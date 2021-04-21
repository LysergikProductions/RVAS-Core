package core.commands;

/* *
 * 
 *  About: A command for ops to repair various naturally-generated structures
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
import core.events.ChunkListener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class Repair implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;
		Chunk chunk = player.getLocation().getChunk();
		
		if (args.length != 0) {
			
			switch (args[0]) {
				case "portals":		
					ChunkListener.fixEndExit(chunk);
					return true;
					
				case "roof":
					ChunkListener.repairBedrockROOF(chunk, player);
					return true;
					
				case "floor":
					ChunkListener.repairBedrockFLOOR(chunk);
					return true;
			}

			// user has submitted an unexpected argument/s
			player.spigot().sendMessage(new TextComponent("Check your spelling!"));
			return true;
			
		} else { // user supplied no arguments
			
			player.spigot().sendMessage(new TextComponent("You must include what to repair!"));
			return true;
		}
	}
}
