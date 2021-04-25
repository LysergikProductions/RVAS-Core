package core.commands;

/* *
 * 
 *  About: A command for ops to backup vital rvas-core data files
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
import core.backend.PlayerMeta;
import core.backend.FileManager;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Backup implements CommandExecutor {
	
	public static int opBackupCounter = 0;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;
		if (!player.isOp() || args.length != 0) return false;
		
		opBackupCounter++;
		
		if (!PlayerMeta.isAdmin(player) && opBackupCounter > 10) {
			player.spigot().sendMessage(
					new TextComponent("Sorry, there have already been at least 10 backups this session."));
			return false;
		}
		
		try {
			player.spigot().sendMessage(new TextComponent("pvpstats.."));
			FileManager.backupData(FileManager.pvpstats_user_database, "pvpstats-backup-", ".txt");
			
			player.spigot().sendMessage(new TextComponent("playtimes.."));
			FileManager.backupData(FileManager.playtime_user_database, "playtimes-backup-", ".db");
			
			player.spigot().sendMessage(new TextComponent("player_settings.."));
			FileManager.backupData(FileManager.settings_user_database, "player_settings-backup-", ".txt");
			
			player.spigot().sendMessage(new TextComponent("muted.."));
			FileManager.backupData(FileManager.muted_user_database, "muted-backup-", ".db");
			
			player.spigot().sendMessage(new TextComponent("donators.."));
			FileManager.backupData(FileManager.donor_list, "donator-backup-", ".db");
			
			//player.spigot().sendMessage(new TextComponent("codes.."));
			//FileManager.backupData(FileManager.all_donor_codes, "codes/all-backup-", ".db");
			
			//player.spigot().sendMessage(new TextComponent("used codes.."));
			//FileManager.backupData(FileManager.used_donor_codes, "codes/used-backup-", ".db");
			
			player.spigot().sendMessage(new TextComponent("analytics.."));
			FileManager.backupData(FileManager.server_statistics_list, "analytics-backup-", ".csv");
			
			player.spigot().sendMessage(new TextComponent("config.."));
			FileManager.backupData(FileManager.core_server_config, "config-backup-", ".txt");
			
			player.spigot().sendMessage(new TextComponent("motds.."));
			FileManager.backupData(FileManager.motd_message_list, "motds-backup-", ".txt");
			
			player.spigot().sendMessage(new TextComponent("prisoners.."));
			FileManager.backupData(FileManager.prison_user_database, "prisoners-backup-", ".db");
			
		} catch (IOException e) {
			
			System.out.println("Could not backup one or more files..");
			System.out.println(e);
			
			player.spigot().sendMessage(new TextComponent("There was an exception while trying to backup one or more files :("));
			return false;
		}
		return true;
	}
}
