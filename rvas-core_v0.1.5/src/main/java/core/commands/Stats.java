package core.commands;

import core.Main;
import core.backend.PlayerMeta;
import core.backend.Utilities;
import core.backend.ChatPrint;
import core.objects.*;

import java.util.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

public class Stats implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		UUID playerid = player.getUniqueId();
		
		PlayerSettings targetSettings = PlayerMeta.sPlayerSettings.get(playerid);
		
		if (targetSettings == null) {
			
			PlayerSettings newSettings = PlayerMeta.getNewSettings(Bukkit.getOfflinePlayer(playerid));
			PlayerMeta.sPlayerSettings.put(playerid, newSettings);
		}

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
					ChatPrint.printStats(player, Main.Top);
					return true;
					
				case "leaderboard":
					ChatPrint.printLeaders(player);
					return true;
					
				case "5":// change printLeaders to be able to take a desired list size argument
					ChatPrint.printLeaders(player);
					return true;
					
				case "help":
					ChatPrint.helpStats(player);
					return true;
					
				case "showkills":	
					
					targetSettings.show_kills = !targetSettings.show_kills;
					
					if (targetSettings.show_kills) {
						
						player.spigot().sendMessage(new TextComponent("Your kills are now public."));
						
					} else if (!targetSettings.show_kills) {
						
						player.spigot().sendMessage(new TextComponent("Your kills are now hidden."));
					}
					
					return true;
					
				case "showdeaths":
					
					targetSettings.show_deaths = !targetSettings.show_deaths;
					
					if (targetSettings.show_deaths) {
						
						player.spigot().sendMessage(new TextComponent("Your deaths are now public."));
						
					} else if (!targetSettings.show_deaths) {
						
						player.spigot().sendMessage(new TextComponent("Your deaths are now hidden."));
					}
					
					return true;
					
				case "showkd":
					
					targetSettings.show_kd = !targetSettings.show_kd;
					
					if (targetSettings.show_kd) {
						
						player.spigot().sendMessage(new TextComponent("Your k/d ratio is now public."));
						
					} else if (!targetSettings.show_kd) {
						
						player.spigot().sendMessage(new TextComponent("Your k/d ratio is now hidden."));
					}
					
					return true;
			}

			// user has submitted a probable username argument
			OfflinePlayer offline_player = Bukkit.getOfflinePlayer(args[0]);
			
			if (offline_player == null ) {
				
				player.spigot().sendMessage(new TextComponent("This player has never joined."));
				return true;
				
			} else if (!offline_player.hasPlayedBefore()) {
				
				player.spigot().sendMessage(new TextComponent("This player has never joined."));
				return true;
			}
			
			ChatPrint.printStats(player, offline_player);
			return true;
			
		} else { // user supplied no arguments
			
			OfflinePlayer target = Bukkit.getOfflinePlayer(player.getUniqueId());
			
			ChatPrint.printStats(player, target);
			return true;
		}
	}
}
