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

import core.backend.Utilities;
import core.events.ChunkListener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class Repair implements CommandExecutor {
	public static int y_default = 63;
	public static int y_low;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;
		Chunk chunk = player.getLocation().getChunk();
		
		if (!player.isOp()) return false;

		y_low = Utilities.getExitFloor(chunk);
		if (y_low == -1) y_low = y_default;

		if (args.length != 0) {
			
			switch (args[0]) {
			
				case "portals":// - Fix all End-related portals in the sender's current *chunk*
					
					ChunkListener.fixEndExit(chunk, y_low);
					return true;
					
				case "exit":// - Fix entire end exit portal when sender is in The End

					Chunk pos_pos = player.getWorld().getChunkAt(0, 0);
					Chunk pos_neg = player.getWorld().getChunkAt(0, -1);
					Chunk ned_pos = player.getWorld().getChunkAt(-1, 0);
					Chunk neg_neg = player.getWorld().getChunkAt(-1, -1);
					
					pos_pos.load(false); // <- load (but don't generate) end chunk's
					ChunkListener.fixEndExit(pos_pos, y_low); // try using onLoad() only, to trigger fixEndExit()
					
					pos_neg.load(false);
					ChunkListener.fixEndExit(pos_neg, y_low);
					
					ned_pos.load(false);
					ChunkListener.fixEndExit(ned_pos, y_low);
					
					neg_neg.load(false);
					ChunkListener.fixEndExit(neg_neg, y_low);
					
					return true;
					
				case "roof":
					ChunkListener.repairBedrockROOF(chunk, player);
					return true;
					
				case "floor":
					ChunkListener.repairBedrockFLOOR(chunk, player);
					return true;
			}

			// user has submitted an unexpected argument/s
			player.spigot().sendMessage(new TextComponent("Check your spelling!"));

		} else { // user supplied no arguments
			
			player.spigot().sendMessage(new TextComponent("You must include what to repair!"));
		}
		return true;
	}
}