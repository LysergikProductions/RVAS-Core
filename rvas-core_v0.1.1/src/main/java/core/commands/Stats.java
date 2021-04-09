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

		//PVPstats stats = PVPstats.fromLine(line);
		//if (stats != null) { PlayerMeta.pvp_stats.put(stats.playerid, stats); }
		
		if (args.length != 0) {
			switch (args[0]) {
				case "top":
					OfflinePlayer largestPlayer = Main.Top;
					Date date = new Date(largestPlayer.getFirstPlayed());
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
					String firstPlayed = sdf.format(date);
					String lastPlayed = sdf.format(new Date(largestPlayer.getLastPlayed()));
					Arrays.asList("--- " + largestPlayer.getName() + "'s Statistics ---",
							"Joined: " + firstPlayed, "Last seen: " + lastPlayed,
							"Ranking: #" + PlayerMeta.getRank(largestPlayer),
							"Time played: " + Utilities.calculateTime(PlayerMeta.getPlaytime(largestPlayer)),
							"Total Kills: " + PlayerMeta.getKills(largestPlayer)
					).forEach(s -> player.spigot().sendMessage(new TextComponent(s)));
					return true;
					
				case "leaderboard":
					player.spigot().sendMessage(new TextComponent("--- Top Five Players ---"));
					HashMap<UUID, Double> leaders = PlayerMeta.getTopFivePlayers();
					int x = 0;
					HashMap<UUID, Double> realLeaders = PlayerMeta.getTopFivePlayers();
					for (UUID u : leaders.keySet()) {
						realLeaders.put(u, leaders.get(u));
					}
					for (UUID p : realLeaders.keySet()) {
						x++;
						player.spigot().sendMessage(Bukkit.getOfflinePlayer(p).getName() == null ?
								new TextComponent(
								"#" + x + ": [unknown], " + Utilities.calculateTime(realLeaders.get(p))) :
								new TextComponent("#" + x + ": " + Bukkit.getOfflinePlayer(p).getName() + ", "
								+ Utilities.calculateTime(realLeaders.get(p))));
					}
					return true;
			}

			OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
			if (p == null) {
				player.spigot().sendMessage(new TextComponent("This player has never joined."));
				return true;
			} else if (!p.hasPlayedBefore()) {
				player.spigot().sendMessage(new TextComponent("This player has never joined."));
				return true;
			}
			Date date = new Date(p.getFirstPlayed());
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
			
			String firstPlayed = sdf.format(date);
			String lastPlayed = sdf.format(new Date(p.getLastPlayed()));
		    List<String> message = new ArrayList<String>();
		    
			message.add("--- " + p.getName() + "'s Statistics ---");
			message.add("Joined: " + firstPlayed);
			message.add("Last seen: " + lastPlayed);
			
			if(!PlayerMeta.Playtimes.containsKey(p.getUniqueId())) {
				message.add("Found no data for this user.");
			} else {
				message.add("Ranking: #" + PlayerMeta.getRank(p));
				message.add("Time played: " + Utilities.calculateTime(PlayerMeta.getPlaytime(p)));
				message.add("Total Kills: " + PlayerMeta.getKills(p));
			}
			message.forEach(s -> player.spigot().sendMessage(new TextComponent(s)));
			return true;
		} else { // user supplied no arguments, so..
			Date date = new Date(player.getFirstPlayed());
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
			
			String firstPlayed = sdf.format(date);
			String lastPlayed = sdf.format(new Date(player.getLastPlayed()));
			
			Arrays.asList("--- " + player.getName() + "'s Statistics ---",
					"Joined: " + firstPlayed,
					"Last seen: " + lastPlayed,
					"Ranking: " + PlayerMeta.getRank(player),
					"Time played: " + Utilities.calculateTime(PlayerMeta.getPlaytime(player)),
					"Total Kills: " + PlayerMeta.getKills(player)
			).forEach(s -> player.spigot().sendMessage(new TextComponent(s)));
			return true;
		}
	}
}
