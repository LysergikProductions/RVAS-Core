package protocol3.commands;

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
import protocol3.Main;
import protocol3.backend.PlayerMeta;
import protocol3.backend.Utilities;

// funny command haha

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

		if (args.length != 0) {
			switch (args[0]) {
				case "top":
					OfflinePlayer largestPlayer = Main.Top;
					Date date = new Date(largestPlayer.getFirstPlayed());
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
					String firstPlayed = sdf.format(date);
					String lastPlayed = sdf.format(new Date(largestPlayer.getLastPlayed()));
					Arrays.asList("§6--- §6§l " + largestPlayer.getName() + "§r§6's Statistics ---",
							"§6Joined: §6§l" + firstPlayed, "§6Last seen: §6§l" + lastPlayed,
							"§6Ranking: §6§l#" + PlayerMeta.getRank(largestPlayer),
							"§6Time played: " + Utilities.calculateTime(PlayerMeta.getPlaytime(largestPlayer)))
							.forEach(s -> player.spigot().sendMessage(
							new TextComponent(s)));
					return true;
				case "leaderboard":
					player.spigot().sendMessage(new TextComponent("§6--- §6§lTop Five Players ---"));
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
								"§6§l#" + x + "§r§6: [unknown], " + Utilities.calculateTime(realLeaders.get(p))) :
								new TextComponent("§6§l#" + x + "§r§6: " + Bukkit.getOfflinePlayer(p).getName() + ", "
								+ Utilities.calculateTime(realLeaders.get(p))));
					}
					return true;
			}

			OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
			if (p == null) {
				player.spigot().sendMessage(new TextComponent("§cThis player has never joined."));
				return true;
			} else if (!p.hasPlayedBefore()) {
				player.spigot().sendMessage(new TextComponent("§cThis player has never joined."));
				return true;
			}
			Date date = new Date(p.getFirstPlayed());
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
			String firstPlayed = sdf.format(date);
			String lastPlayed = sdf.format(new Date(p.getLastPlayed()));
		    List<String> message = new ArrayList<String>();
			message.add("§6--- §6§l " + p.getName() + "§r§6's Statistics ---");
			message.add("§6Joined: §6§l" + firstPlayed);
			message.add("§6Last seen: §6§l" + lastPlayed);
			
			if(!PlayerMeta.Playtimes.containsKey(p.getUniqueId())) {
				message.add("§6§oSome statistics cannot be shown (untracked).");
			}
			else {
				message.add("§6Ranking: §6§l#" + PlayerMeta.getRank(p));
				message.add("§6Time played: " + Utilities.calculateTime(PlayerMeta.getPlaytime(p)));
			}
			message.forEach(s -> player.spigot().sendMessage(new TextComponent(s)));
			return true;
		} else {
			Date date = new Date(player.getFirstPlayed());
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
			String firstPlayed = sdf.format(date);
			String lastPlayed = sdf.format(new Date(player.getLastPlayed()));
			Arrays.asList("§6--- §6§l " + player.getName() + "§r§6's Statistics ---",
					"§6Joined: §6§l" + firstPlayed,
					"§6Last seen: §6§l" + lastPlayed,
					"§6Ranking: §6§l#" + PlayerMeta.getRank(player),
					"§6Time played: " + Utilities.calculateTime(PlayerMeta.getPlaytime(player)))
					.forEach(s -> player.spigot().sendMessage(new TextComponent(s)));
			return true;
		}
	}

}
