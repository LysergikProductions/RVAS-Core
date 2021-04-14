package core.backend;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;
import java.text.SimpleDateFormat;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;

import core.events.Chat;
import core.backend.Config;
import core.backend.PlayerMeta;
import core.backend.Utilities;

public class ChatPrint {
	public static void printLeaders(Player receiver) {
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
				
			} else {// name != null
				
				int kills = PlayerMeta.getStats(Bukkit.getOfflinePlayer(pid)).killTotal;
				int deaths = PlayerMeta.getStats(Bukkit.getOfflinePlayer(pid)).deathTotal;
				String kd = PlayerMeta.getStats(Bukkit.getOfflinePlayer(pid)).kd;
				
				TextComponent b = new TextComponent(Bukkit.getOfflinePlayer(pid).getName() + ", " + Utilities.calculateTime(realLeaders.get(pid)));
				TextComponent c = new TextComponent(a, b);
				
				HoverEvent hoverStats = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Kills: "+kills+" | Deaths: "+deaths+" | K/D: "+kd));
				
				c.setColor(ChatColor.GOLD);
				c.setHoverEvent(hoverStats);
				
				list.add(c);
			}
		}
		list.forEach(ln -> receiver.spigot().sendMessage(ln));
	}
	
	public static void printStats(Player receiver, OfflinePlayer target) {
		
		Player player = target.getPlayer();
		
		Date date = new Date(target.getFirstPlayed());
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		
		String firstPlayed = sdf.format(date);
		String lastPlayed = sdf.format(new Date(target.getLastPlayed()));
		OfflinePlayer largestPlayer = target;
		
		// get all uniquley stylable components
		TextComponent title_pre = new TextComponent("--- ");
		TextComponent title_name = new TextComponent(target.getName());
		TextComponent title_suf = new TextComponent("'s Statistics ---");
		
		TextComponent joined_a = new TextComponent("Joined: ");
		TextComponent joined_b = new TextComponent(firstPlayed);
		TextComponent lastSeen_a = new TextComponent("Last seen: ");
		TextComponent lastSeen_b = new TextComponent(lastPlayed);
		TextComponent rank_a = new TextComponent("Ranking: ");
		TextComponent rank_b = new TextComponent("" + PlayerMeta.getRank(target));
		TextComponent playtime_a = new TextComponent("Time played: ");
		TextComponent playtime_b = new TextComponent(Utilities.calculateTime(PlayerMeta.getPlaytime(target)));
		TextComponent toptime_b = new TextComponent(Utilities.calculateTime(PlayerMeta.getPlaytime(largestPlayer)));
		
		TextComponent tkills_a = new TextComponent("Total PVP Kills: ");
		TextComponent tkills_b = new TextComponent("" + PlayerMeta.getStats(target).killTotal);
		TextComponent tdeaths_a = new TextComponent("Total PVP Deaths: ");
		TextComponent tdeaths_b = new TextComponent("" + PlayerMeta.getStats(target).deathTotal);
		TextComponent kd_a = new TextComponent("K/D: ");
		TextComponent kd_b = new TextComponent("" + PlayerMeta.getStats(target).kd);
		
		// style individual components
		joined_a.setColor(ChatColor.BLUE); joined_a.setBold(true);
		
		lastSeen_a.setColor(ChatColor.BLUE); lastSeen_a.setBold(true);
		
		rank_a.setColor(ChatColor.BLUE); rank_a.setBold(true);
		
		playtime_a.setColor(ChatColor.BLUE); playtime_a.setBold(true);
		
		tkills_a.setColor(ChatColor.BLUE); tkills_a.setBold(true);
		
		tdeaths_a.setColor(ChatColor.BLUE); tdeaths_a.setBold(true);
		
		// parse components into 1-line components
		TextComponent title = new TextComponent(title_pre, title_name, title_suf);
		TextComponent joined = new TextComponent(joined_a, joined_b);
		TextComponent lastSeen = new TextComponent(lastSeen_a, lastSeen_b);
		TextComponent rank = new TextComponent(rank_a, rank_b);
		TextComponent playtime = new TextComponent(playtime_a, playtime_b);
		TextComponent toptime = new TextComponent(playtime_a, toptime_b);
		TextComponent tkills = new TextComponent(tkills_a, tkills_b);
		TextComponent tdeaths = new TextComponent(tdeaths_a, tdeaths_b);
		TextComponent kd = new TextComponent(kd_a, kd_b);
		
		// style lines of multiple components at once
		title.setColor(ChatColor.YELLOW); title.setBold(true);
		kd.setColor(ChatColor.GRAY); kd_a.setBold(false);
		
		// send final message to receiver
		Arrays.asList(title, joined, lastSeen, rank, playtime, tkills, tdeaths, kd)
		.forEach(ln -> receiver.spigot().sendMessage(ln));
	}
}
