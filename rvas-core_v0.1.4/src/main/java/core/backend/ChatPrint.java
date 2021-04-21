package core.backend;

/* *
 * 
 *  About: Store void methods that print information
 *  	from RVAS-Core to a given user's chat
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

import core.events.Chat;
import core.backend.Config;
import core.backend.PlayerMeta;
import core.backend.PVPdata;
import core.backend.Utilities;

import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;

public class ChatPrint {
	
	// - HELP PAGES - //
	
	public static void helpGeneral(Player receiver, int page) {
		return;
	}
	
	public static void helpStats(Player receiver) {
		
		TextComponent head = new TextComponent("--- /stats help ---");
		
		TextComponent self_a = new TextComponent("/stats");
		TextComponent players_a = new TextComponent("/stats [player name]");
		TextComponent leaders_a = new TextComponent("/stats 5");
		
		TextComponent self_b = new TextComponent(" : Shows you your stats");
		TextComponent players_b = new TextComponent(" : Shows the stats for that player");
		TextComponent leaders_b = new TextComponent(" : Shows the top 5 players (by play-time)");
		
		self_b.setColor(ChatColor.GRAY);
		players_b.setColor(ChatColor.GRAY);
		leaders_b.setColor(ChatColor.GRAY);
		
		self_a.setItalic(true);
		players_a.setItalic(true);
		leaders_a.setItalic(true);
		
		TextComponent self = new TextComponent(self_a, self_b);
		TextComponent players = new TextComponent(players_a, players_b);
		TextComponent leaders = new TextComponent(leaders_a, leaders_b);
		
		receiver.spigot().sendMessage(head);
		
		HoverEvent hover_leaders = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new Text("Click on a player in the leaderboard to see their stats quickly"));
		
		leaders.setHoverEvent(hover_leaders);
		
		ArrayList<TextComponent> list = new ArrayList<>();
		list.add(self); list.add(leaders); list.add(players);
		
		list.forEach(ln -> receiver.spigot().sendMessage(ln));
	}
	
	// - STATS PAGES - //
	
	public static void printLeaders(Player receiver) {
		
		HashMap<UUID, Double> leaders = PlayerMeta.getTopFivePlayers();
		HashMap<UUID, Double> realLeaders = PlayerMeta.getTopFivePlayers();
		
		for (UUID u : leaders.keySet()) {
			realLeaders.put(u, leaders.get(u));
		}
		
		int x = 0;
		ArrayList<TextComponent> list = new ArrayList<>();
		
		for (UUID pid : realLeaders.keySet()) {
			x++;
			
			TextComponent a1 = new TextComponent("#" + x + ": "); a1.setBold(true);
			OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(pid);
			String target_name = offPlayer.getName();
			
			if (target_name == null) {
				
				TextComponent b = new TextComponent("[unknown], " + Utilities.calculateTime(realLeaders.get(pid)));
				TextComponent c = new TextComponent(a1, b);
				
				c.setColor(ChatColor.GOLD);
				
				list.add(c);
				
			} else {// this leader name != null
				
				int kills = PVPdata.getStats(offPlayer).killTotal;
				int deaths = PVPdata.getStats(offPlayer).deathTotal;
				String kd = PVPdata.getStats(offPlayer).kd;
				
				TextComponent a2 = new TextComponent(target_name + ", ");
				TextComponent b = new TextComponent(Utilities.calculateTime(realLeaders.get(pid)));
				
				HoverEvent hoverStats = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Kills: "+kills+" | Deaths: "+deaths+" | K/D: "+kd));
				ClickEvent shortcut = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stats " + target_name);
				
				a2.setColor(ChatColor.GOLD);
				b.setItalic(true);
				b.setHoverEvent(hoverStats);
				
				TextComponent c = new TextComponent(a1, a2, b);
				
				c.setClickEvent(shortcut);
				list.add(c);
			}
		}
		
		TextComponent top5_head = new TextComponent("--- Top Five Players ---");		
		TextComponent ujoins_a = new TextComponent("Unique Joins: ");
		TextComponent ujoins_b = new TextComponent("" + PlayerMeta.Playtimes.keySet().size());
		TextComponent msg = new TextComponent(ujoins_a, ujoins_b);

		top5_head.setColor(ChatColor.GOLD); top5_head.setBold(true);
		msg.setColor(ChatColor.GRAY); msg.setItalic(true);
		
		receiver.spigot().sendMessage(top5_head);
		list.forEach(ln -> receiver.spigot().sendMessage(ln));
		receiver.spigot().sendMessage(msg);
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
		
		TextComponent tkills_a = new TextComponent("PVP Kills: ");
		TextComponent tkills_b = new TextComponent("" + PVPdata.getStats(target).killTotal);
		TextComponent tdeaths_a = new TextComponent("PVP Deaths: ");
		TextComponent tdeaths_b = new TextComponent("" + PVPdata.getStats(target).deathTotal);
		
		String spawnKills = String.valueOf(PVPdata.getStats(target).spawnKills);
		HoverEvent hover_killDetail = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Spawn Kills: " + spawnKills));
		
		// style individual components
		joined_a.setColor(ChatColor.BLUE); joined_a.setBold(true);
		
		lastSeen_a.setColor(ChatColor.BLUE); lastSeen_a.setBold(true);
		
		rank_a.setColor(ChatColor.BLUE); rank_a.setBold(true);
		
		playtime_a.setColor(ChatColor.BLUE); playtime_a.setBold(true);
		
		tkills_a.setColor(ChatColor.BLUE); tkills_a.setBold(true);
		tkills_b.setHoverEvent(hover_killDetail);
		
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
		TextComponent kd;
		
		try {
			kd = new TextComponent("K/D: " + new DecimalFormat("#.###").format(Double.parseDouble(PVPdata.getStats(target).kd)));
		} catch (NumberFormatException e) {
			kd = new TextComponent("K/D: " + PVPdata.getStats(target).kd);
		}
		
		title.setColor(ChatColor.YELLOW); title.setBold(true);
		kd.setColor(ChatColor.GRAY);
		
		// send final message to receiver
		Arrays.asList(title, joined, lastSeen, rank, playtime, tkills, tdeaths, kd)
		.forEach(ln -> receiver.spigot().sendMessage(ln));
	}
}
