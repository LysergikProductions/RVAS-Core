package core.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import core.Main;
import core.backend.PlayerMeta;
import core.backend.Utilities;

public class Stats implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;

		if (Main.Top == null) {
			double largest = 0;
			
			for (UUID u : PlayerMeta.Playtimes.keySet()) {
				if (PlayerMeta.Playtimes.get(u) > largest) {
					largest = PlayerMeta.Playtimes.get(u);
					Main.Top = Bukkit.getOfflinePlayer(u);
				}
			}
		}

		Date date = new Date(player.getFirstPlayed());
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		
		String firstPlayed = sdf.format(date);
		String lastPlayed = sdf.format(new Date(player.getLastPlayed()));
		OfflinePlayer largestPlayer = Main.Top;
		
		// get all uniquley stylable components
		TextComponent title_pre = new TextComponent("--- ");
		TextComponent title_name = new TextComponent(player.getName());
		
		TextComponent title_suf = new TextComponent("'s Statistics ---");
		TextComponent top5_head = new TextComponent("--- Top Five Players ---");
		
		TextComponent joined_a = new TextComponent("Joined: ");
		TextComponent joined_b = new TextComponent(firstPlayed);
		TextComponent lastSeen_a = new TextComponent("Last seen: ");
		TextComponent lastSeen_b = new TextComponent(lastPlayed);
		TextComponent rank_a = new TextComponent("Ranking: ");
		TextComponent rank_b = new TextComponent("" + PlayerMeta.getRank(player));
		TextComponent playtime_a = new TextComponent("Time played: ");
		TextComponent playtime_b = new TextComponent(Utilities.calculateTime(PlayerMeta.getPlaytime(player)));
		TextComponent toptime_b = new TextComponent(Utilities.calculateTime(PlayerMeta.getPlaytime(largestPlayer)));
		TextComponent tkills_a = new TextComponent("Total PVP Kills: ");
		TextComponent tkills_b = new TextComponent("" + PlayerMeta.getKills(player));
		
		// style individual components
		joined_a.setColor(ChatColor.BLUE); joined_a.setBold(true);
		
		lastSeen_a.setColor(ChatColor.BLUE); lastSeen_a.setBold(true);
		
		rank_a.setColor(ChatColor.BLUE); rank_a.setBold(true);
		
		playtime_a.setColor(ChatColor.BLUE); playtime_a.setBold(true);
		
		tkills_a.setColor(ChatColor.BLUE); tkills_a.setBold(true);
		
		top5_head.setColor(ChatColor.GOLD); top5_head.setBold(true);
		
		// parse components into 1-line components
		TextComponent title = new TextComponent(title_pre, title_name, title_suf);
		TextComponent joined = new TextComponent(joined_a, joined_b);
		TextComponent lastSeen = new TextComponent(lastSeen_a, lastSeen_b);
		TextComponent rank = new TextComponent(rank_a, rank_b);
		TextComponent playtime = new TextComponent(playtime_a, playtime_b);
		TextComponent toptime = new TextComponent(playtime_a, toptime_b);
		TextComponent tkills = new TextComponent(tkills_a, tkills_b);
		
		// style lines of multiple components at once
		title.setColor(ChatColor.YELLOW); title.setBold(true);
		
		// check args
		if (args.length != 0) {
			switch (args[0]) {
				case "top":
					Arrays.asList(title, joined, lastSeen, rank, toptime, tkills)
					.forEach(ln -> player.spigot().sendMessage(ln));
					return true;
					
				case "leaderboard":
					player.spigot().sendMessage(top5_head);
					HashMap<UUID, Double> leaders = PlayerMeta.getTopFivePlayers();
					int x = 0;
					HashMap<UUID, Double> realLeaders = PlayerMeta.getTopFivePlayers();
					for (UUID u : leaders.keySet()) {
						realLeaders.put(u, leaders.get(u));
					}
					
					ArrayList<TextComponent> list = new ArrayList<>();
					for (UUID pid : realLeaders.keySet()) {
						x++;
						TextComponent a = new TextComponent("#" + x + ": "); a.setBold(true);
						
						if (Bukkit.getOfflinePlayer(pid).getName() == null) {
							TextComponent b = new TextComponent("[unknown], " + Utilities.calculateTime(realLeaders.get(pid)));
							TextComponent c = new TextComponent(a, b);
							
							c.setColor(ChatColor.GOLD);
							list.add(c);
						} else {
							TextComponent b = new TextComponent(Bukkit.getOfflinePlayer(pid).getName() + ", " + Utilities.calculateTime(realLeaders.get(pid)));
							TextComponent c = new TextComponent(a, b);
							
							c.setColor(ChatColor.GOLD);
							list.add(c);
						}
					}
					list.forEach(ln -> player.spigot().sendMessage(ln));
					return true;
			}

			// user has submitted a username argument, so..
			OfflinePlayer p2 = Bukkit.getOfflinePlayer(args[0]);
			
			Date datewarg = new Date(p2.getFirstPlayed());
			String firstPlayedwarg = sdf.format(date);
			String lastPlayedwarg = sdf.format(new Date(p2.getLastPlayed()));
			
			TextComponent title_namewarg = new TextComponent(p2.getName());
			TextComponent joinedwarg_b = new TextComponent(firstPlayedwarg);
			TextComponent lastSeenwarg_b = new TextComponent(lastPlayedwarg);
			TextComponent rankwarg_b = new TextComponent("" + PlayerMeta.getRank(p2));
			TextComponent playtimewarg_b = new TextComponent(Utilities.calculateTime(PlayerMeta.getPlaytime(p2)));
			TextComponent tkillswarg_b = new TextComponent("" + PlayerMeta.getKills(p2));
			
			TextComponent titlewarg = new TextComponent(title_pre, title_namewarg, title_suf);
			TextComponent joinedwarg = new TextComponent(joined_a, joinedwarg_b);
			TextComponent lastSeenwarg = new TextComponent(lastSeen_a, lastSeenwarg_b);
			TextComponent rank_warg = new TextComponent(rank_a, rankwarg_b);
			TextComponent playtime_warg = new TextComponent(playtime_a, playtimewarg_b);
			TextComponent tkills_warg = new TextComponent(tkills_a, tkillswarg_b);	
			
			titlewarg.setColor(ChatColor.YELLOW); titlewarg.setBold(true);
			
			if (p2 == null) {
				player.spigot().sendMessage(new TextComponent("This player has never joined."));
				return true;
			} else if (!p2.hasPlayedBefore()) {
				player.spigot().sendMessage(new TextComponent("This player has never joined."));
				return true;
			}
			
			Arrays.asList(titlewarg, joinedwarg, lastSeenwarg)
			.forEach(ln -> player.spigot().sendMessage(ln));
			
			if(!PlayerMeta.Playtimes.containsKey(p2.getUniqueId())) {
				player.spigot().sendMessage(new TextComponent("Found no data for this user."));
			} else {
				Arrays.asList(rank_warg, playtime_warg, tkills_warg)
				.forEach(ln -> player.spigot().sendMessage(ln));
			}
			return true;
		} else { // user supplied no arguments, so..
			Arrays.asList(title, joined, lastSeen, rank, playtime, tkills)
			.forEach(ln -> player.spigot().sendMessage(ln));
			return true;
		}
	}
}
