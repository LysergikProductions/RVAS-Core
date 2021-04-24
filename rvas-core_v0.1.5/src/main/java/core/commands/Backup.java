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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Backup implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;
		
		if (!player.isOp() || args.length != 0) return false;
		//if (!PlayerMeta.isAdmin(player)) return false;
		
		try {
			
			FileManager.backupData(FileManager.pvpstats_user_database, "pvpstats-backup-", ".txt");
			FileManager.backupData(FileManager.playtime_user_database, "playtimes-backup-", ".db");
			FileManager.backupData(FileManager.settings_user_database, "player_settings-backup-", ".txt");
			FileManager.backupData(FileManager.muted_user_database, "muted-backup-", ".db");
			
			FileManager.backupData(FileManager.donor_list, "donator-backup-", ".db");
			FileManager.backupData(FileManager.all_donor_codes, "codes/all-backup-", ".db");
			FileManager.backupData(FileManager.used_donor_codes, "codes/used-backup-", ".db");
			
			FileManager.backupData(FileManager.server_statistics_list, "analytics-backup-", ".csv");
			FileManager.backupData(FileManager.core_server_config, "config-backup-", ".txt");
			FileManager.backupData(FileManager.motd_message_list, "motds-backup-", ".txt");
			//FileManager.backupData(FileManager.prison_user_database, "prisoners-backup-", ".db");
			
		} catch (IOException e) {
			
			System.out.println("Could not backup files..");
			System.out.println(e);
			return false;
		}
		return true;
	}
}
