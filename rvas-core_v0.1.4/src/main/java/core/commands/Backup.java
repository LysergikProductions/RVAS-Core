package core.commands;

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
			
		} catch (IOException e) {
			
			System.out.println("Could not backup files..");
			System.out.println(e);
			return false;
		}
		return true;
	}
}
