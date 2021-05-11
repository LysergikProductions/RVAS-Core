package core.commands;

import core.Main;
import core.backend.*;
import core.objects.*;
import core.tasks.Analytics;

import java.util.*;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

public class Stats implements CommandExecutor {
	
	public static int sessionUses = 0;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sessionUses++;
		
		Player player = (Player) sender;
		UUID playerid = player.getUniqueId();
		
		if (!PlayerMeta.isAdmin(player)) Analytics.stats_total++;
		
		PlayerSettings targetSettings = PlayerMeta.sPlayerSettings.get(playerid);
		if (targetSettings == null) {
			
			PlayerSettings newSettings = PlayerMeta.getNewSettings(Bukkit.getOfflinePlayer(playerid));
			PlayerMeta.sPlayerSettings.put(playerid, newSettings);
		}
		
		// top player by playtime
		if (Main.Top == null) {
			double largest = 0;
			
			for (UUID u : PlayerMeta.Playtimes.keySet()) {
				
				if (PlayerMeta.Playtimes.get(u) > largest) {
					largest = PlayerMeta.Playtimes.get(u);
					Main.Top = Bukkit.getOfflinePlayer(u);
				}
			}
		}
		
		// check args
		if (args.length != 0) {
			
			switch (args[0]) {
				case "top":
					assert Main.Top != null;
					ChatPrint.printStats(player, Main.Top);
					return true;
					
				case "leaderboard":
				case "5":
					ChatPrint.printLeaders(player);
					return true;

				case "help":
					HelpPages.helpStats(player);
					if (!PlayerMeta.isAdmin(player)) Analytics.stats_help++;
					return true;
					
				case "kills":

					assert targetSettings != null;
					targetSettings.show_kills = !targetSettings.show_kills;
					
					if (targetSettings.show_kills) {
						
						player.spigot().sendMessage(new TextComponent("Your kills are now public."));
						
					} else {
						
						player.spigot().sendMessage(new TextComponent("Your kills are now hidden."));
					}
					
					return true;
					
				case "deaths":

					assert targetSettings != null;
					targetSettings.show_deaths = !targetSettings.show_deaths;
					
					if (targetSettings.show_deaths) {
						
						player.spigot().sendMessage(new TextComponent("Your deaths are now public."));
						
					} else {
						
						player.spigot().sendMessage(new TextComponent("Your deaths are now hidden."));
					}
					
					return true;
					
				case "kd":

					assert targetSettings != null;
					targetSettings.show_kd = !targetSettings.show_kd;
					
					if (targetSettings.show_kd) {
						
						player.spigot().sendMessage(new TextComponent("Your k/d ratio is now public."));
						
					} else {
						
						player.spigot().sendMessage(new TextComponent("Your k/d ratio is now hidden."));
					}
					
					return true;
					
				case "mc":
					
					ChatPrint.printMcStats(player, Bukkit.getOfflinePlayer(player.getUniqueId()));					
					return true;
				
				case "info":
				case "settings":
					
					ChatPrint.printPlayerSettings(player);
					if (!PlayerMeta.isAdmin(player)) Analytics.stats_info++;
					return true;
			}

			// user has submitted a probable username argument
			OfflinePlayer offline_player = Bukkit.getOfflinePlayer(args[0]);
			
			if (offline_player == null || !offline_player.hasPlayedBefore()) {
				
				player.spigot().sendMessage(new TextComponent("This player has never joined."));
				return true;
			}
			
			ChatPrint.printStats(player, offline_player);

		} else { // user supplied no arguments
			
			OfflinePlayer target = Bukkit.getOfflinePlayer(player.getUniqueId());
			
			ChatPrint.printStats(player, target);
		}
		return true;
	}
}
