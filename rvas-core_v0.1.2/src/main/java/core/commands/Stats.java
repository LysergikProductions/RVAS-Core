package core.commands;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import core.Main;
import core.backend.PlayerMeta;
import core.backend.Utilities;
import core.backend.ChatPrint;

public class Stats implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		
		TextComponent top5_head = new TextComponent("--- Top Five Players ---");
		top5_head.setColor(ChatColor.GOLD); top5_head.setBold(true);

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
					player.spigot().sendMessage(top5_head);
					ChatPrint.printLeaders(player);
					return true;
					
				case "5":// change printLeaders to be able to take a desired list size argument
					player.spigot().sendMessage(top5_head);
					ChatPrint.printLeaders(player);
					return true;
			}

			// user has submitted a username argument, so..
			OfflinePlayer p2 = Bukkit.getOfflinePlayer(args[0]);
			if (p2 == null ) {
				
				player.spigot().sendMessage(new TextComponent("This player has never joined."));
				return true;
			} else if (!p2.hasPlayedBefore()) {
				
				player.spigot().sendMessage(new TextComponent("This player has never joined."));
				return true;
			}
			
			ChatPrint.printStats(player, p2);
			return true;
			
		} else {
			// user supplied no arguments, so..
			OfflinePlayer target = Bukkit.getOfflinePlayer(player.getUniqueId());
			
			ChatPrint.printStats(player, target);
			return true;
		}
	}
}
