package core.commands.op;

/* *
 *  About: A command for ops to see current session data
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

import core.frontend.ChatPrint;
import core.backend.ServerMeta;
import core.backend.utils.Util;

import core.events.ChunkManager;
import core.events.BlockListener;
import core.events.SpawnController;

import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Info implements CommandExecutor {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
		
		Player player = (Player) sender;
		if (!player.isOp()) return false;
			
		String humanUptime = Util.durationFormat(ServerMeta.getUptime());
		
		TextComponent head = new TextComponent("--- Session Stats ---");
		head.setColor(ChatPrint.primary); head.setBold(true);
		
		player.sendMessage(head.toLegacyText());
		player.sendMessage("Uptime: " + humanUptime);
		player.sendMessage("New Chunks: " + ChunkManager.newCount);
		player.sendMessage("New Players: " + SpawnController.sessionNewPlayers);
		player.sendMessage("Total Respawns: " + SpawnController.sessionTotalRespawns);
		
		player.sendMessage("Bedrock Placed: " + BlockListener.placedBedrockCounter);
		player.sendMessage("Bedrock Broken: " + BlockListener.brokenBedrockCounter);
		// TODO : add total BlockPlaceEvent's and BlockBreakEvent's
		
		return true;
	}
}
