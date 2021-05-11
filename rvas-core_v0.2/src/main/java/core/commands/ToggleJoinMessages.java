package core.commands;

/* *
 * 
 *  About: A simple command for players to toggle seeing join/leave messages
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

import core.backend.PlayerMeta;
import core.objects.PlayerSettings;
import core.tasks.Analytics;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class ToggleJoinMessages implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		Player player = (Player) sender;
		if (!PlayerMeta.isAdmin(player)) Analytics.tjm_cmd++;
		
		PlayerSettings theseSettings = PlayerMeta.sPlayerSettings.get(player.getUniqueId());
		
		if (theseSettings != null) {
			theseSettings.show_player_join_messages = !theseSettings.show_player_join_messages;
			
		} else {
			theseSettings = PlayerMeta.getNewSettings(Bukkit.getOfflinePlayer(player.getUniqueId()));
			
			theseSettings.show_player_join_messages = false; // default is true, so this cmd should set setting of new users of cmd to false
			PlayerMeta.sPlayerSettings.put(theseSettings.playerid, theseSettings);
		}
		
		if (theseSettings.show_player_join_messages) {
			
			player.spigot().sendMessage(new TextComponent("§6Enabled join and leave messages."));
			
		} else {
			
			player.spigot().sendMessage(new TextComponent("§6Disabled join and leave messages."));
		}
		return true;
	}
}
