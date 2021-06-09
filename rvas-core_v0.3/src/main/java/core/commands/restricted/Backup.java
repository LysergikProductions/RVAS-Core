package core.commands.restricted;

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
import core.data.PlayerMeta;
import core.data.FileManager;

import java.io.IOException;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class Backup implements CommandExecutor {
	
	public static int opBackupCounter = 0;
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		
		Player player = (Player) sender;
		if (!player.isOp() || args.length != 0) return false;
		
		opBackupCounter++;
		
		if (!PlayerMeta.isAdmin(player) && opBackupCounter > 8) {
			player.sendMessage(
					"Sorry, there have already been at least 8 backups this session.");
			return false;
		}
		
		try {
			player.sendMessage("backing up pvpstats.txt");
			FileManager.backupData(FileManager.pvpstats_user_database, "pvpstats-backup-", ".txt");
			
			player.sendMessage("backing up playtimes.db");
			FileManager.backupData(FileManager.playtime_user_database, "playtime-backup-", ".db");
			
			player.sendMessage("backing up player-settings.txt");
			FileManager.backupData(FileManager.settings_user_database, "player_settings-backup-", ".txt");
			
			player.sendMessage("backing up muted players.db");
			FileManager.backupData(FileManager.muted_user_database, "muted-backup-", ".db");
			
			player.sendMessage("backing up donators.json");
			FileManager.backupData(FileManager.donor_database, "donator-backup-", ".json");
			
			player.sendMessage("backing up prisoners.db");
			FileManager.backupData(FileManager.prison_user_database, "prisoners-backup-", ".db");

			player.sendMessage("backing up custom.json");
			FileManager.backupData(FileManager.prison_user_database, "prisoners-backup-", ".db");
			
		} catch (IOException e) {
			
			System.out.println("Could not backup one or more files..");
			if (Config.debug) e.printStackTrace();
			
			player.sendMessage("There was an exception while trying to backup one or more files :(");
			return false;

		} return true;
	}
}
